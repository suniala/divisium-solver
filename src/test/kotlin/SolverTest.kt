import org.example.Cell
import org.example.Puzzle
import org.example.Triplet
import org.example.findLinearTriplets
import org.example.findSolution
import org.example.findTriplets
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

class SolverTest {
    @Test
    fun neighbours() {
        val puzzle = Puzzle.fromString(
            """
            0 2 1
            1 1 3
            1 0 0
        """.trimIndent()
        )

        assertAll(
            {
                assertEquals(
                    setOf(
                        Cell(row = 0, col = 1, value = 2),
                        Cell(row = 1, col = 0, value = 1),
                    ),
                    puzzle.neighbours(puzzle.cells.first())
                )
            },
            {
                assertEquals(
                    setOf(
                        Cell(row = 0, col = 1, value = 2),
                        Cell(row = 1, col = 0, value = 1),
                        Cell(row = 1, col = 2, value = 3),
                        Cell(row = 2, col = 1, value = 0),
                    ),
                    puzzle.neighbours(puzzle.cells[4])
                )
            }
        )
    }

    @Test
    fun findLinearTriplets() {
        val puzzle = Puzzle.fromString(
            """
            0 2 1
            1 1 3
            1 0 0
            """.trimIndent()
        )

        assertEquals(
            setOf(
                Triplet.of(
                    Cell(row = 0, col = 0, value = 0),
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 0, col = 2, value = 1)
                ),
                Triplet.of(
                    Cell(row = 0, col = 0, value = 0),
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 1, col = 1, value = 1)
                ),
            ),
            findLinearTriplets(listOf(puzzle.cells.first()), setOf(puzzle.cells.first()), puzzle)
        )
    }

    @Test
    fun findTriplets() {
        val puzzle = Puzzle.fromString(
            """
            0 2 1
            1 1 3
            1 0 0
            """.trimIndent()
        )

        val candidates = findTriplets(puzzle)
        assertEquals(
            setOf(
                Triplet.of(
                    Cell(row = 0, col = 0, value = 0),
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 0, col = 2, value = 1)
                ),
                Triplet.of(
                    Cell(row = 0, col = 0, value = 0),
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 1, col = 0, value = 1)
                ),
                Triplet.of(
                    Cell(row = 0, col = 0, value = 0),
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 1, col = 1, value = 1)
                ),
                Triplet.of(
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 1, col = 1, value = 1),
                    Cell(row = 2, col = 1, value = 0)
                ),
                Triplet.of(
                    Cell(row = 1, col = 0, value = 1),
                    Cell(row = 1, col = 1, value = 1),
                    Cell(row = 2, col = 0, value = 1)
                ),
                Triplet.of(
                    Cell(row = 1, col = 2, value = 3),
                    Cell(row = 2, col = 1, value = 0),
                    Cell(row = 2, col = 2, value = 0)
                ),
            ).formatForComparison(),
            candidates.formatForComparison()
        )
    }

    @Test
    fun findSolution3x3() {
        val puzzle = Puzzle.fromString(
            """
            0 2 1
            1 1 3
            1 0 0
            """
        )

        val solution = findSolution(puzzle)
        assertEquals(
            setOf(
                Triplet.of(
                    Cell(row = 0, col = 0, value = 0),
                    Cell(row = 0, col = 1, value = 2),
                    Cell(row = 0, col = 2, value = 1)
                ),
                Triplet.of(
                    Cell(row = 1, col = 0, value = 1),
                    Cell(row = 1, col = 1, value = 1),
                    Cell(row = 2, col = 0, value = 1)
                ),
                Triplet.of(
                    Cell(row = 1, col = 2, value = 3),
                    Cell(row = 2, col = 1, value = 0),
                    Cell(row = 2, col = 2, value = 0)
                ),
            ).formatForComparison(),
            solution.formatForComparison()
        )
    }

    @Test
    fun findSolution6x6() {
        val puzzle = Puzzle.fromString(
            """
            0 1 0 0 1 2
            2 3 0 1 0 2
            2 1 3 2 3 1
            0 0 0 0 0 3
            3 0 1 3 0 0
            0 2 0 0 0 0
            """
        )

        val solution = findSolution(puzzle)
        assertEquals(
            setOf(
                Triplet.of(
                    Cell(0, 0, 0),
                    Cell(0, 1, 1),
                    Cell(1, 0, 2),
                ),
                Triplet.of(
                    Cell(0, 2, 0),
                    Cell(1, 1, 3),
                    Cell(1, 2, 0),
                ),
                Triplet.of(
                    Cell(0, 3, 0),
                    Cell(0, 4, 1),
                    Cell(0, 5, 2),
                ),
                Triplet.of(
                    Cell(1, 3, 1),
                    Cell(2, 3, 2),
                    Cell(3, 3, 0),
                ),
                Triplet.of(
                    Cell(1, 4, 0),
                    Cell(1, 5, 2),
                    Cell(2, 5, 1),
                ),
                Triplet.of(
                    Cell(2, 0, 2),
                    Cell(2, 1, 1),
                    Cell(3, 0, 0),
                ),
                Triplet.of(
                    Cell(2, 2, 3),
                    Cell(3, 1, 0),
                    Cell(3, 2, 0),
                ),
                Triplet.of(
                    Cell(2, 4, 3),
                    Cell(3, 4, 0),
                    Cell(4, 4, 0),
                ),
                Triplet.of(
                    Cell(3, 5, 3),
                    Cell(4, 5, 0),
                    Cell(5, 5, 0),
                ),
                Triplet.of(
                    Cell(4, 0, 3),
                    Cell(4, 1, 0),
                    Cell(5, 0, 0),
                ),
                Triplet.of(
                    Cell(4, 2, 1),
                    Cell(5, 1, 2),
                    Cell(5, 2, 0),
                ),
                Triplet.of(
                    Cell(4, 3, 3),
                    Cell(5, 3, 0),
                    Cell(5, 4, 0),
                ),
            ).formatForComparison(),
            solution.formatForComparison()
        )
    }

    companion object {
        fun Collection<Triplet>.formatForComparison(): String = this
            .sortedBy { triplet -> triplet.cells.map { """${it.row}_${it.col}""" }.sorted().joinToString(",") }
            .map { triple ->
                val formattedCells = triple.cells
                    .sortedBy { """${it.row}_${it.col}""" }
                    .map { cell ->
                        """  Cell(${cell.row}, ${cell.col}=${cell.value})"""
                    }
                    .joinToString("\n")
                """|Triplet.of(
                   |$formattedCells
                   |)
                """.trimMargin()
            }
            .joinToString("\n")
    }
}
