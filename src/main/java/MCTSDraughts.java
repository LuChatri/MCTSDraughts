public class MCTSDraughts {

    public static void main(String[] args) {
        GameState gs = null;
        try {
            gs = new GameState(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Please input a valid draughts FEN in argument 0.");
            System.exit(1);
        }

        long duration = 0;
        try {
            duration = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Please input a valid draughts FEN in argument 1");
            System.exit(1);
        }

        try {
            MCTSDraughtsSearcher searcher = new MCTSDraughtsSearcher(gs, Math.sqrt(2.0));
            long endTime = System.currentTimeMillis() + duration;

            while (System.currentTimeMillis() < endTime) {
                searcher.searchOnce();
            }

            System.out.println("Best Moves: " + searcher.getBestMoves());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred. Time ot open an issue on Github.");
            e.printStackTrace();
        }
    }

}