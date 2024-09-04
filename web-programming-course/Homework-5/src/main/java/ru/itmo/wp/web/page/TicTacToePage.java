package ru.itmo.wp.web.page;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    private void action(HttpServletRequest request, Map<String, Object> view) {
        view.put("state", getState(request));
    }

    private State getState(HttpServletRequest request) {
        if (request.getSession().getAttribute("state") == null) {
            request.getSession().setAttribute("state", new State());
        }
        return (State) request.getSession().getAttribute("state");
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        State state = getState(request);
        for (int i = 0; i < state.size; i++) {
            for (int j = 0; j  < state.size; j++) {
                if (request.getParameter("cell_"+ i + j) != null) {
                    state.move(i, j);
                    action(request, view);
                    return;
                }
            }
        }
    }

    private void newGame(HttpServletRequest request, Map<String, Object> view) {
        request.getSession().setAttribute("state", new State());
        action(request, view);
    }

    public static class State {
        private int count;
        public final int size;
        public boolean crossesMove;
        public String phase;
        public String[][] cells;

        public State() {
            count = 0;
            size = 3;
            crossesMove = true;
            phase = "RUNNING";
            cells = new String[size][size];
        }

        public int getSize() {
            return size;
        }

        public boolean getCrossesMove() {
            return crossesMove;
        }

        public String getPhase() {
            return phase;
        }

        public String[][] getCells() {
            return cells;
        }

        private boolean checkCell(final int i, final int j, final String turn) {
            return cells[i][j] != null && cells[i][j].equals(turn);
        }

        private void checkWin(final String turn) {
            for (int i = 0; i < size; i++) {
                if (checkCell(i, 0, turn) && checkCell(i, 1, turn) && checkCell(i, 2, turn) ||
                        (checkCell(0, i, turn) && checkCell(1, i, turn) && checkCell(2, i, turn))) {
                    phase = "WON_" + turn;
                    return;
                }
            }
            if (checkCell(0, 0, turn) && checkCell(1, 1, turn) && checkCell(2, 2, turn) ||
                    (checkCell(0, 2, turn) && checkCell(1, 1, turn) && checkCell(2, 0, turn))) {
                phase = "WON_" + turn;
                return;
            }
            if (count == size * size) {
                phase = "DRAW";
            }
        }

        private boolean between(int x) {
            return 0 <= x && x < size;
        }

        public void move(final int i, final int j) {
            if (phase.equals("RUNNING") && cells[i][j] == null) {
                final String turn = crossesMove ? "X" : "O";
                cells[i][j] = turn;
                count++;
                checkWin(turn);
                crossesMove = !crossesMove;
            }
        }
    }
}
