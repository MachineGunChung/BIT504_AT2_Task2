import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel implements MouseListener {
    // Constants for game
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final String TITLE = "Tic Tac Toe";

    // Constants for dimensions used for drawing
    public static final int CELL_SIZE = 100;
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static final int CELL_PADDING = CELL_SIZE / 6;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
    public static final int SYMBOL_STROKE_WIDTH = 8;

    /* Declare game object variables */
    private Board board;

    // Enum for GameState (Playing, Draw, Cross_won, Nought_won)
    private GameState currentState;

    // Enum for current Player (Cross or Nought)
    private Player currentPlayer;

    // JLabel for displaying game status
    private JLabel statusBar;

    /** Constructor to setup the UI and game components on the panel */
    public GameMain() {
        // Add mouse listener to this JPanel
        addMouseListener(this);

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("         ");
        statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
        statusBar.setOpaque(true);
        statusBar.setBackground(Color.LIGHT_GRAY);

        // Layout of the panel is in border layout
        setLayout(new BorderLayout());
        add(statusBar, BorderLayout.SOUTH);
        // Account for statusBar height in overall height
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 30));

        // Create a new instance of the board and initialize the game
        board = new Board();
        initGame();
    }

    public static void main(String[] args) {
        // Run GUI code in Event Dispatch thread for thread safety.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create a main window to contain the panel
                JFrame frame = new JFrame(TITLE);
                frame.add(new GameMain());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    /** Custom painting code on this JPanel */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        board.paint(g);

        // Set status bar message
        if (currentState == GameState.Playing) {
            statusBar.setForeground(Color.BLACK);
            if (currentPlayer == Player.Cross) {
                statusBar.setText("X's Turn");
            } else {
                statusBar.setText("O's Turn");
            }
        } else if (currentState == GameState.Draw) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == GameState.Cross_won) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == GameState.Nought_won) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    /** Initialize the game-board contents and the current status of GameState and Player */
    public void initGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board.cells[row][col].content = Player.Empty;
            }
        }
        currentState = GameState.Playing;
        currentPlayer = Player.Cross;
    }

    /** After each turn check to see if the current player has won */
    public void updateGame(Player thePlayer, int row, int col) {
        // Check for win after play
        if (board.hasWon(thePlayer, row, col)) {
            if (thePlayer == Player.Cross) {
                currentState = GameState.Cross_won;
            } else {
                currentState = GameState.Nought_won;
            }
        } else if (board.isDraw()) {
            currentState = GameState.Draw;
        }
    }

    /** Event handler for the mouse click on the JPanel */
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        int rowSelected = mouseY / CELL_SIZE;
        int colSelected = mouseX / CELL_SIZE;

        if (currentState == GameState.Playing) {
            if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0 && colSelected < COLS && board.cells[rowSelected][colSelected].content == Player.Empty) {
                board.cells[rowSelected][colSelected].content = currentPlayer;
                updateGame(currentPlayer, rowSelected, colSelected);

                // Switch player
                currentPlayer = (currentPlayer == Player.Cross) ? Player.Nought : Player.Cross;
            }
        } else {
            initGame();
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

}

// Enum for GameState (Playing, Draw, Cross_won, Nought_won)
enum GameState {
    Playing,
    Draw,
    Cross_won,
    Nought_won
}

// Enum for Player (Empty, Cross, Nought)
enum Player {
    Empty,
    Cross,
    Nought
}

// Cell class for representing each individual cell in the board
class Cell {
    public Player content;

    public Cell() {
        this.content = Player.Empty;
    }
}

// Board class for managing the game board
class Board {
    public Cell[][] cells;

    public Board() {
        cells = new Cell[GameMain.ROWS][GameMain.COLS];
        for (int row = 0; row < GameMain.ROWS; ++row) {
            for (int col = 0; col < GameMain.COLS; ++col) {
                cells[row][col] = new Cell();
            }
        }
    }

    public void paint(Graphics g) {
        // Draw the grid
        for (int row = 0; row < GameMain.ROWS; ++row) {
            for (int col = 0; col < GameMain.COLS; ++col) {
                g.setColor(Color.BLACK);
                g.drawRect(col * GameMain.CELL_SIZE, row * GameMain.CELL_SIZE, GameMain.CELL_SIZE, GameMain.CELL_SIZE);
                if (cells[row][col].content == Player.Cross) {
                    g.setColor(Color.RED);
                    g.drawLine(col * GameMain.CELL_SIZE + GameMain.CELL_PADDING, row * GameMain.CELL_SIZE + GameMain.CELL_PADDING, (col + 1) * GameMain.CELL_SIZE - GameMain.CELL_PADDING, (row + 1) * GameMain.CELL_SIZE - GameMain.CELL_PADDING);
                    g.drawLine((col + 1) * GameMain.CELL_SIZE - GameMain.CELL_PADDING, row * GameMain.CELL_SIZE + GameMain.CELL_PADDING, col * GameMain.CELL_SIZE + GameMain.CELL_PADDING, (row + 1) * GameMain.CELL_SIZE - GameMain.CELL_PADDING);
                } else if (cells[row][col].content == Player.Nought) {
                    g.setColor(Color.BLUE);
                    g.drawOval(col * GameMain.CELL_SIZE + GameMain.CELL_PADDING, row * GameMain.CELL_SIZE + GameMain.CELL_PADDING, GameMain.SYMBOL_SIZE, GameMain.SYMBOL_SIZE);
                }
            }
        }
    }

    public boolean hasWon(Player player, int row, int col) {
        // Check row, column, and diagonals for a win
        return (checkRow(player, row) || checkColumn(player, col) || checkDiagonals(player));
    }

    public boolean checkRow(Player player, int row) {
        for (int col = 0; col < GameMain.COLS; ++col) {
            if (cells[row][col].content != player) {
                return false;
            }
        }
        return true;
    }

    public boolean checkColumn(Player player, int col) {
        for (int row = 0; row < GameMain.ROWS; ++row) {
            if (cells[row][col].content != player) {
                return false;
            }
        }
        return true;
    }

    public boolean checkDiagonals(Player player) {
        if (cells[0][0].content == player && cells[1][1].content == player && cells[2][2].content == player) {
            return true;
        }
        if (cells[0][2].content == player && cells[1][1].content == player && cells[2][0].content == player) {
            return true;
        }
        return false;
    }

    public boolean isDraw() {
        for (int row = 0; row < GameMain.ROWS; ++row) {
            for (int col = 0; col < GameMain.COLS; ++col) {
                if (cells[row][col].content == Player.Empty) {
                    return false;
                }
            }
        }
        return true;
    }
}
