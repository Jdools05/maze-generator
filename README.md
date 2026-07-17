# Maze Generator

A Java maze generator built with [Processing](https://processing.org/). It generates solvable mazes using a randomized depth-first search (recursive backtracker) algorithm and renders them interactively via the Processing GUI.

## Features

- **Maze generation** — Randomized DFS / recursive backtracker algorithm produces a perfect maze where every cell is reachable from every other cell.
- **Shortest path solver** — Computes and highlights the shortest path from the start to the exit.
- **Distance gradient visualization** — Tiles are colored based on their distance (in steps) from the exit, giving an intuitive heat-map of how far each region lies from the goal.
- **Interactive GUI** — Rendered with Processing's `PApplet`. Press **`R`** to regenerate a new maze on the fly.
- **Benchmarking mode** — Run headless (no GUI) to generate multiple mazes in quick succession and measure average generation time per maze.

## Project Structure

```
src/
├── Main.java              — Entry point; launches the Processing sketch.
├── MazeGenerator.java     — Core logic: maze creation, pathfinding, benchmarking.
├── Graphics.java          — Rendering layer (Processing PApplet).
├── Tile.java              — Individual cell: walls, visited state, distance from exit.
└── Direction.java         — Enum for the four cardinal directions (UP/DOWN/LEFT/RIGHT).
```

## Prerequisites

- [Java](https://www.oracle.com/java/) 8+
- [Processing](https://processing.org/download) library (included as a dependency in the project)

## Running

### With GUI

Run `Main.java` from your IDE or via the command line. The maze renders on screen and you can press **`R`** to regenerate it.

```bash
# Example: run with a sample size of 10 for benchmarking (no GUI shown, results printed to console)
java Main 10 noGui
```

### Headless / Benchmark Mode

Pass an integer argument to generate that many mazes back-to-back and print timing statistics:

```bash
java Main 50 noGui
```

Console output looks like:

```
Reloaded in: 42ms

Elapsed maze generation time in milliseconds: 1876ms
Mazes created: 50
Average time: 37.52ms
Created 400 cells per maze (20 X 20)
```

## Maze Parameters

The default maze is **20 × 20** tiles, with the start positioned at `(0, height/2)` and a random exit along the right edge. These can be tweaked in `MazeGenerator.java`:

| Field | Default | Description |
|---|---|---|
| `mazeWidth` | `20` | Number of columns |
| `mazeHeight` | `20` | Number of rows |
| `startX` | `0` | Start column |
| `startY` | `height / 2` | Start row |
| `exitX` | `width - 1` | Exit column (right edge) |
| `exitY` | random | Exit row (randomized on each generation) |

## Algorithm Overview

1. **Generation** — Starts from the exit cell, performing a randomized DFS: at each step it picks an unvisited neighbor, carves the wall between them, and moves forward. When stuck, it backtracks until all cells are visited.
2. **Shortest path** — Greedily walks from start toward the exit by always stepping to the neighboring tile with the smallest `stepsToExit` value (computed during generation).

## Colors

| Element | Color | Purpose |
|---|---|---|
| Background | Black (`#000000`) | Base canvas |
| Walls | White (`#FFFFFF`) | Maze walls |
| Start tile | Green (`#00FF00`) | Entrance |
| Exit tile | Yellow (`#FFFF00`) | Goal |
| Shortest path | Green line | Solved route |
| Interior tiles | Purple gradient | Distance from exit (darker = farther) |

## License

This project is open source.
