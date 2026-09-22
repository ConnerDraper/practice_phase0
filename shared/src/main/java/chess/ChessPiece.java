package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceType type = this.getPieceType();
        boolean repeat;
        Collection<ChessMove> moves = new ArrayList<>();
        int row;
        int column;

        ChessGame.TeamColor myTeam = this.getTeamColor();

        if (type == PieceType.PAWN) {
            int startingRow;
            int forwardDirection;
            ChessPosition currPosition;
            ChessGame.TeamColor otherColor;
            int promotionRow;

            if (myTeam == ChessGame.TeamColor.WHITE) {
                startingRow = 2;
                forwardDirection = 1;
                otherColor = ChessGame.TeamColor.BLACK;
                promotionRow = 8;
            } else {
                startingRow = 7;
                forwardDirection = -1;
                otherColor = ChessGame.TeamColor.WHITE;
                promotionRow = 1;
            }
            row = myPosition.getRow();
            column = myPosition.getColumn();

            // single forward and two forward
            if (board.getPiece(row + forwardDirection, column) == null) {
                ChessPosition frontSpace = new ChessPosition(row + 1, column);
                ChessMove frontMove = new ChessMove(myPosition, frontSpace, null);
                moves.add(frontMove);
                if (row == startingRow) {
                    if (board.getPiece(row + (2 * forwardDirection), column) == null) {
                        ChessPosition twoFrontSpace = new ChessPosition(row + 2, column);
                        ChessMove twoFrontMove = new ChessMove(myPosition, twoFrontSpace, null);
                        moves.add(twoFrontMove);
                    }
                }
            }

            // diagonal capture LEFT
            if (column > 1) {
                ChessPiece leftPiece = board.getPiece(row + forwardDirection, column - 1);
                if (leftPiece != null) {
                    if (leftPiece.getTeamColor() == otherColor) {
                        if (row == (promotionRow - forwardDirection)) {
                            ChessPosition leftPosition = new ChessPosition(row + forwardDirection, column-1);
                            moves.add(new ChessMove(myPosition, leftPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, leftPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, leftPosition, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, leftPosition, PieceType.BISHOP));
                        } else {
                            ChessPosition leftPosition = new ChessPosition(row + forwardDirection, column-1);
                            moves.add(new ChessMove(myPosition, leftPosition, null));
                        }
                    }
                }
            }

            // diagonal capture RIGHT
            if (column < 8) {
                ChessPiece rightPiece = board.getPiece(row + forwardDirection, column - 1);
                if (rightPiece != null) {
                    if (rightPiece.getTeamColor() == otherColor) {
                        if (row == (promotionRow - forwardDirection)) {
                            ChessPosition rightPosition = new ChessPosition(row + forwardDirection, column+1);
                            moves.add(new ChessMove(myPosition, rightPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, rightPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, rightPosition, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, rightPosition, PieceType.BISHOP));
                        } else {
                            ChessPosition rightPosition = new ChessPosition(row + forwardDirection, column+1);
                            moves.add(new ChessMove(myPosition, rightPosition, null));
                        }
                    }
                }
            }
        }
        else if (type == PieceType.ROOK) {
            int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            repeat = true;
            moves = getMovesFromDirections(board, myPosition, directions, repeat);
        }
        else if (type == PieceType.KNIGHT) {
            int[][] directions = {
                    {1, 2}, {-1, 2}, {1, -2}, {-1, -2},
                    {2, 1}, {-2, 1}, {2, -1}, {-2, -1},
            };
            repeat = false;
            moves = getMovesFromDirections(board, myPosition, directions, repeat);
        }
        else if (type == PieceType.BISHOP) {
            int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, 1}};
            repeat = true;
            moves = getMovesFromDirections(board, myPosition, directions, repeat);
        }
        else if (type == PieceType.QUEEN) {
            int[][] directions = {
                    {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                    {1, 1}, {-1, 1}, {1, -1}, {-1, 1}
            };
            repeat = true;
            moves = getMovesFromDirections(board, myPosition, directions, repeat);
        }
        else if (type == PieceType.KING) {
            int[][] directions = {
                    {1, 1}, {1, 0}, {1, -1},
                    {0, 1},         {0, -1},
                    {-1, 1}, {-1, 0}, {-1, -1}
            };
            repeat = false;
            moves = getMovesFromDirections(board, myPosition, directions, repeat);
        }
        return moves;
    }

    /**
     * Helper function for pieceMoves()
     * Calculates all positions a chess piece can move to
     * Takes in a direction array and a repeat boolean
     * Uses said objects to determine all possible valid moves
     * Does not account for illegal moves that violate king's safety, etc.
     * @return Collection of valid moves
     */
    public Collection<ChessMove> getMovesFromDirections(
            ChessBoard board,
            ChessPosition myPosition,
            int[][] directions,
            boolean repeat) {
        int currRow;
        int currColumn;
        ChessPiece currPiece;
        List<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currTeam;
        ChessGame.TeamColor otherTeam;
        ChessGame.TeamColor myTeam = this.getTeamColor();
        ChessMove proposedMove;
        ChessPosition currPosition;

        if (myTeam == ChessGame.TeamColor.WHITE) otherTeam = ChessGame.TeamColor.BLACK;
        else otherTeam = ChessGame.TeamColor.WHITE;

        for (int[] d : directions) {
            int rowDelta = d[0];
            int columnDelta = d[1];

            for (int i = 1; i <= 8; i++) {
                currRow = myPosition.getRow() + (i * rowDelta);
                currColumn = myPosition.getColumn() + (i * columnDelta);

                // validate not out of bounds
                if ((currRow < 1) || (currRow > 8) || (currColumn < 1) || (currColumn > 8)) {break;}
                currPiece = board.getPiece(currPosition);
                proposedMove = new ChessMove(myPosition, currPosition, null);

                // validate if empty or capturable
                if (currPiece == null) {
                    moves.add(proposedMove);
                }
                else {
                    currTeam = currPiece.getTeamColor();
                    if (currTeam == otherTeam) {
                        moves.add(proposedMove);
                    }
                }
                if (!repeat) {break;}
            }
        }
        return moves;
    }
}
