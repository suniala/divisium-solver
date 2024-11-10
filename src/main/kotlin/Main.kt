package org.example

data class Puzzle(val cells: List<Cell>) {
    fun neighbours(cell: Cell): Set<Cell> = cells
        .filter {
            (it.col == cell.col && (it.row == (cell.row - 1) || it.row == (cell.row + 1)))
                    || (it.row == cell.row && (it.col == (cell.col - 1) || it.col == (cell.col + 1)))
        }
        .filter { it != cell }
        .toSet()

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

data class Cell(val row: Int, val col: Int, val value: Int) {
    companion object {
        fun sum(cells: Collection<Cell>): Int = cells.map(Cell::value).sum()
    }
}

data class Triplet(val cells: Set<Cell>) {
    init {
        require(cells.size == 3) {
            "Size is not 3: $cells"
        }
        require(Cell.sum(cells) == 3) {
            "Sum is not 3: $cells"
        }
    }

    override fun toString(): String {
        return "Triplet(cells=${cells.sortedBy { "${it.row}_$it.col" }})"
    }

    companion object {
        fun of(c1: Cell, c2: Cell, c3: Cell) = Triplet(setOf(c1, c2, c3))
        fun of(cells: Collection<Cell>) = Triplet(cells.toSet())
    }
}

fun findTriplets(puzzle: Puzzle): Set<Triplet> {
    val a = puzzle.cells
        .asSequence()
        .flatMap { cell ->
            findLinearTriplets(listOf(cell), setOf(cell), puzzle)
        }
        .toSet()
    return a
}

fun findLinearTriplets(tripletAcc: List<Cell>, visited: Set<Cell>, puzzle: Puzzle): Set<Triplet> {
    return if (tripletAcc.size == 3 && Cell.sum(tripletAcc) == 3) {
        setOf(Triplet.of(tripletAcc))
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
                    visited = visited + neighbours,
                    puzzle = puzzle
                )
            }
            .toSet()
    }
}

fun findSolution(puzzle: Puzzle): Set<Triplet> {
    val triplets = findTriplets(puzzle)
    return findWithOnlyOneChoice(emptySet(), triplets)
}

fun findWithOnlyOneChoice(acc: Set<Triplet>, rem: Set<Triplet>): Set<Triplet> {
    return if (rem.isEmpty()) {
        acc
    } else {
        val (_, _, foundTriplet) = rem
            .flatMap { triplet -> triplet.cells.map { cell -> cell to triplet } }
            .toSet()
            .map { (cell, triplet) ->
                Triple(cell, rem.count { otherTriplet -> otherTriplet.cells.contains(cell) }, triplet)
            }
            .firstOrNull { (_, tripletCount) -> tripletCount == 1 }
            ?: throw IllegalStateException("Must find at least one cell that is in only one triplet\nacc=${acc}\nrem=${rem}")

        val remWithoutFoundCells = rem
            .asSequence()
            .filter { triplet -> triplet.cells.none { cell -> cell in foundTriplet.cells }}
            .toSet()

        findWithOnlyOneChoice(acc + foundTriplet, remWithoutFoundCells)
    }
}