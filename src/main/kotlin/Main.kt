package org.example

import java.util.*

data class Puzzle(val cells: List<Cell>) {
    fun neighbours(cell: Cell): SortedSet<Cell> = cells
        .filter {
            (it.col == cell.col && (it.row == (cell.row - 1) || it.row == (cell.row + 1)))
                    || (it.row == cell.row && (it.col == (cell.col - 1) || it.col == (cell.col + 1)))
        }
        .filter { it != cell }
        .toSortedSet()

    companion object {
        fun fromString(s: String): Puzzle = Puzzle(
            s.lines()
                .asSequence()
                .map(String::trim)
                .filter(String::isNotEmpty)
                .map { row -> row.split(" ") }
                .mapIndexed { row, cells ->
                    cells.mapIndexed { col, cell ->
                        Cell(row, col, cell.toInt())
                    }
                }
                .flatten()
                .toList()
        )
    }
}

data class Cell(val row: Int, val col: Int, val value: Int) : Comparable<Cell> {
    override fun compareTo(other: Cell): Int = comparator.compare(this, other)

    companion object {
        private val comparator = compareBy(Cell::row).thenBy(Cell::col)
        fun sum(cells: Collection<Cell>): Int = cells.map(Cell::value).sum()
    }
}

data class Triplet(val cells: SortedSet<Cell>) : Comparable<Triplet> {
    init {
        require(cells.size == 3) {
            "Size is not 3: $cells"
        }
        require(Cell.sum(cells) == 3) {
            "Sum is not 3: $cells"
        }
    }

    override fun compareTo(other: Triplet): Int =
        cells
            .asSequence()
            .zip(other.cells.asSequence())
            .map { (a, b) -> a.compareTo(b) }
            .firstOrNull { it != 0 }
            ?: 0

    override fun toString(): String {
        return "Triplet(cells=${cells.sortedBy { "${it.row}_$it.col" }})"
    }

    companion object {
        fun of(c1: Cell, c2: Cell, c3: Cell) = Triplet(sortedSetOf(c1, c2, c3))
        fun of(cells: Collection<Cell>) = Triplet(cells.toSortedSet())
    }
}

fun findTriplets(puzzle: Puzzle): SortedSet<Triplet> =
    puzzle.cells
        .asSequence()
        .flatMap { cell ->
            findLinearTriplets(listOf(cell), sortedSetOf(cell), puzzle)
        }
        .toSortedSet()

fun findLinearTriplets(tripletAcc: List<Cell>, visited: SortedSet<Cell>, puzzle: Puzzle): SortedSet<Triplet> {
    return if (tripletAcc.size == 3 && Cell.sum(tripletAcc) == 3) {
        sortedSetOf(Triplet.of(tripletAcc))
    } else {
        val currCell = tripletAcc.last()
        val neighbours = puzzle.neighbours(currCell)
        val fittingNeighbours = neighbours
            .filterNot { visited.contains(it) }
            .filter { Cell.sum(tripletAcc) + it.value <= 3 }
        fittingNeighbours
            .asSequence()
            .flatMap { neighbour ->
                findLinearTriplets(
                    tripletAcc = tripletAcc + neighbour,
                    visited = (visited + neighbours).toSortedSet(),
                    puzzle = puzzle
                )
            }
            .toSortedSet()
    }
}

fun findSolution(puzzle: Puzzle): SortedSet<Triplet>? {
    val triplets = findTriplets(puzzle)

    require(puzzle.cells.count() % 3 == 0)
    val solutionTripletCount = puzzle.cells.count() / 3

    return findSolution(solutionTripletCount, sortedSetOf(), triplets)
}

fun findSolution(solutionTripletCount: Int, acc: SortedSet<Triplet>, rem: SortedSet<Triplet>): SortedSet<Triplet>? {
    return if (acc.size + rem.size < solutionTripletCount) {
        null
    } else if (rem.isEmpty()) {
        acc
    } else {
        val cellsTriplets = rem
            .flatMap { triplet -> triplet.cells.map { cell -> cell to triplet } }
            .groupBy(Pair<Cell, Triplet>::first) { it.second }

        val cellsInLeastTriplets = cellsTriplets.asSequence()
            .map { (key, value) -> key to value }
            .sortedWith(compareBy({ it.second.size }, { it.first }))
            .toList()

        val (_, foundTriplets) = cellsInLeastTriplets.first()
        when (foundTriplets.size) {
            0 -> throw IllegalStateException("")
            1 -> {
                val foundTriplet = foundTriplets.single()
                val remWithoutFoundCells = rem
                    .asSequence()
                    .filter { triplet -> triplet.cells.none { cell -> cell in foundTriplet.cells } }
                    .toSortedSet()

                findSolution(solutionTripletCount, (acc + foundTriplet).toSortedSet(), remWithoutFoundCells)
            }

            else -> {
                foundTriplets.firstNotNullOfOrNull { candidate ->
                    val remWithoutFoundCells = rem
                        .asSequence()
                        .filter { triplet -> triplet.cells.none { cell -> cell in candidate.cells } }
                        .toSortedSet()

                    findSolution(solutionTripletCount, (acc + candidate).toSortedSet(), remWithoutFoundCells)
                }
            }
        }
    }
}
