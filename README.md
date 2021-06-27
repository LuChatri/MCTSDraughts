# MCTSDraughts

An exceedingly simple implementation of Monte Carlo Tree Search with UCT (Upper Confidence Bound applied to Trees) for 8x8 English draughts, also known as American draughts, English checkers, or American checkers.

## Usage

For now, you have to build the project yourself. Run the engine as follows:

`java -jar MCTSDraughts.jar [FEN] [Time to Analyze (milliseconds)]`

Input a FEN as seen [here](https://en.wikipedia.org/wiki/Portable_Draughts_Notation#Tag_Pairs). For example:

```
>>> java -jar MCTSDraughts.jar W:W21,22,23,24,25,26,27,28,29,30,31,32:B1,2,3,4,5,6,7,8,9,10,11,12 100000
Best Moves: [Move: 22-->MOVE_NORTHWEST]
```

The output of MCTSDraughts specifies the best move (or, if several moves tie, best moves) for the input position. Each move has a starting location given in [standard checkers notation](https://commons.wikimedia.org/wiki/File:Draughts_Notation.svg) followed by deltas (`MOVE_NORTHWEST`, `JUMP_SOUTHEAST`, etc). In the example above, the best move is to move the piece at square 22 northwest from white's point of view.
