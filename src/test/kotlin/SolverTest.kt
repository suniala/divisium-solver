import org.example.Cell
import org.example.Puzzle
import org.example.Triplet
import org.example.findLinearTriplets
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

    companion object {
        fun Collection<Triplet>.formatForComparison(): String = this
            .sortedBy { triplet -> triplet.cells.map { """${it.row}_${it.col}""" }.sorted().joinToString(",") }
            .map { triple ->
                val formattedCells = triple.cells
                    .sortedBy { """${it.row}_${it.col}""" }
                    .map { cell ->
                        """  C(${cell.row}, ${cell.col}=${cell.value})"""
                    }
                    .joinToString("\n")
                """|T(
                   |$formattedCells
                   |)
                """.trimMargin()
            }
            .joinToString("\n")
    }
}