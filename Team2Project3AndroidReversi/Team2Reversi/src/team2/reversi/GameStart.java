package team2.reversi;

import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

class Global2 {

	// Global Variables //Default case, P1=Black=Human, P2=WHITE=EASYAI. //P1
	// ALWAYS goes first
	static int boardSize = 8, randomMove, maxDepth = 4, maxDepthX = 6,
			maxDepthY = 4, maxDepthZ = 4, totalExecutions = 1, blackWins = 0,
			whiteWins = 0, alphaBeta = -9999;
	static boolean displayOn = true, test = false, server = false;
	static Square CurrentPlayer = Square.player1; // Indicates whose turn it
													// currently is. Game always
													// starts with P1, who is
													// always BLACK.
	static String defaultAISetting = "EASY";
	static String AIlevelP1 = "OFF"; // Defaults P2 'OFF' i.e. P2==Human.
	static String AIlevelP2 = defaultAISetting; // //Defaults P2 to EASY AI.
												// EASY, MEDIUM, HARD, HARDdebg,
												// ... { P1EASY, P2HARD,
												// P2HARD3,...}(commands. ie to
												// make player2 a hard level AI
												// that looks ahead 3 plys)
	static String NullString = "NULL";
	static Pair coordinate = new Pair();
	static Pair bestMove = new Pair();

	static GameReversi newBoard = new GameReversi(boardSize);
	static GameReversi game = new GameReversi(boardSize);

}

class gameStart {

	public// used to determine if the passed player is one of the AIs
	boolean PlayerIsAI(Square Player) {
		if (Player == Square.player1) {
			if(Global2.AIlevelP1 != "OFF") {
				return true;
			} else {
				return false;
			}
			
		} else if (Player == Square.player2) {
			
			if(Global2.AIlevelP2 != "OFF") {
				return true;
			} else {
				return false;
			}

		} else {
			System.err.print("Error: Player passed into --bool isPlayerAI(Square Player)-- must be either player1 or player2.\n");
			return false;
		}
	}

	boolean isPlayerAI(Square Player) {
		return PlayerIsAI(Player);
	}

	// returns the name of the AI as a string, EASY, MEDIUM, HARD
	String AIlevel(Square Player) { // Getter/Setter i.e. void MakeP1Human(){
									// AIlevel(player1)="OFF";}
		if (!isPlayerAI(Player)) {
			// std::cerr<<"Error: Player passed into --std::string& AIlevel(Square Player)-- is not AI.\n";
			System.err
					.println("Error: Player passed into --String AIlevel(Square Player)-- is not AI.\n");
			return Global2.NullString;
		}
		if (Player == Square.player1) {
			return Global2.AIlevelP1;
		} else if (Player == Square.player2) {
			return Global2.AIlevelP2;
		} else {
			// std::cerr<<"Error: Player passed into --std::string& AIlevel(Square Player)-- must be either player1 or player2.\n";
			System.err
					.println("Error: Player passed into --std::string& AIlevel(Square Player)-- must be either player1 or player2.\n");
			return Global2.NullString;
		}
	}

	void AIlevelSet(Square player, String value) {
		if(player == Square.player1) {
			Global2.AIlevelP1 = value;
		} else if(player == Square.player2) {
			Global2.AIlevelP2 = value;
		} else {
			System.err.println("Error in AIlevelSet, nothing satisfied the if-else tree\n");
		}
	}
	
	// << override for Reversi object squares
	void printSquare(Square s) {
		switch (s) {
		case empty:
			System.out.print("_");
			break;
		case validMove:
			System.out.print("x");
			break;
		case player1:
			System.out.print("O");
			break; // char(2); break;
		case player2:
			System.out.print("@");
			break; // char(1); break;
		default:
			System.err.println("Should not get here");
			break;
		}
	}

	// << override for Reversi object board outlay
	void printReversi(GameReversi r) {
		
		System.out.print("  ");
		for(int i = 0; i != r.size; ++i) {
			System.out.print("_ ");
		}
		System.out.print("\n");
		
		for(int i = 0; i != r.size; ++i) {
			System.out.print(i + "|");
			for(int j = 0; j != r.size; ++j) {
				switch(r.mBoard.elementAt(j).elementAt(i)) {
				case empty: System.out.print("_|"); break;
				case player1: System.out.print("O|"); break;
				case player2: System.out.print("@|"); break;
				default: System.out.print("!|"); break;
				}
			}
			System.out.print('\n');
		}
		
		System.out.print("  a b c d e f g h \n");
		
	}

	// Returns true if the string can be converted to integers
	boolean IsInt(String s, int rInt) {
		// istringstream i(s);
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException nFE) {
			return false;
		}
	}

	// Handles all input and drops the newline char. Either recv or getlines.
	String GetInput(int client) {
		String input;
		Scanner sc = new Scanner(System.in);
		input = sc.nextLine();
		input.toUpperCase();
		return input;
	}

	// Handles all output. Takes a string and either cout or sends
	void PrintOut(String inString, int client) {
		System.out.print(inString);
	}

	// Breaks up the coordinate input for an x and y value
	public Vector<String> SeperateString(String input, char seperator) {
		// assert(input.empty()==false);
		// assert(input[0]!=Separator);
		// assert(input[input.size()-1]!=Separator);

		Vector<String> result = new Vector<String>();
		int pos = 0;
		while (pos < (input.length())) {
			if (input.charAt(pos) == seperator) {
				String found = input.substring(0, pos);
				result.add(found);
				input = input.substring(pos + 1, input.length() - pos);
				pos = 0;
			}
			++pos;
		}
		result.add(input);
		return result;
	}

	// Up front determination whether the users coordinate is a valid input type
	boolean IsCoordinate(String input, Pair coordinate) {
		if ((input.length() != 3 && Global2.server)
				|| (input.length() != 2 && !Global2.server))
			return false;
		// TODO: need error (bounds) checking on x and y
		int x, y;
		x = Character.toUpperCase(input.charAt(0)) - 65;
		y = Integer.parseInt(input.substring(1, 2));

		if ((x == 0 || (x > 0 && x < 8)) && (y == 0 || (y > 0 && y < 8))) {

			System.out.print("coordinate " + x + " and " + y + "\n");
			coordinate.Set(y, x); // <- confusing, i know
			return true;
		} else {
			return true;
		}
	}

	// Optional menu option allowing for board sizes between 4X4 and 16X16
	int AskUserForBoardSize(int client) {
		// Get the board's size
		while (true) {
			// cout << "Please enter the size of the reversi board" <<
			// std::endl;
			System.out.println("Please enter the size of the reversi board\n");
			String input = GetInput(client);
			int size = -1;
			if (IsInt(input, size) == false) {
				System.out.print("Please enter an integer value. \n");
				continue;
			}
			if (size < 4) {
				System.out
						.println("Please enter an integer value bigger than 4. \n");
				continue;
			}
			if (size > 16) {
				System.out
						.print("Please enter an integer value less than 16. \n");
				continue;
			}
			return size;
		}
	}

	// inserts the possibleMove symbol into the proper locations of the board
	void showPossibleMoves(int client){
	GameReversi tempValid = new GameReversi(Global2.boardSize);
	tempValid.SetBoard(Global2.game.GetBoard());
	
	Vector<Pair> vals = tempValid.GetValidMoves(Global2.CurrentPlayer);
	for(int i=0; i<vals.size(); i++)
		tempValid.SetSquare(vals.elementAt(i).GetX(),vals.elementAt(i).GetY(),Square.validMove);
	printReversi(tempValid);
	System.out.print("\n");
}

	// uses the current time as a randomizing seed to select from available
	// moves
	void moveRandomly(){
		GameReversi tempValid = new GameReversi(Global2.boardSize);
		tempValid.SetBoard(Global2.game.GetBoard());
		Vector<Pair> vals = tempValid.GetValidMoves(Global2.CurrentPlayer);
		Random generator = new Random(System.currentTimeMillis());
		int randomMove = generator.nextInt(vals.size());
		
		Global2.coordinate.Set(vals.elementAt(randomMove));
	
	}

	// takes input from the user that will set definitions for the game settings
	int handlePregameInput(int client) {
		while (true) {

			String input = GetInput(client);

			if (input.equalsIgnoreCase("EXIT")) {
				return 0;
			}

			else if (input.equalsIgnoreCase("DISPLAY_ON")) {
				Global2.displayOn = true;
				System.out.print("OK\n");
				break;
			}

			else if (input.equalsIgnoreCase("DISPLAY_OFF")) {
				Global2.displayOn = false;
				System.out.print("OK\n");
				break;
			}

			else if (input.equalsIgnoreCase("BLACK")) {
				Global2.AIlevelP1 = Global2.AIlevelP2;
				Global2.AIlevelP2 = "OFF";
				
				System.out.print("BLACK\n");
			}

			else if (input.equalsIgnoreCase("WHITE")) {
				Global2.AIlevelP2 = Global2.AIlevelP1;		
				Global2.AIlevelP1 = "OFF";
				System.out.print("WHITE\n");
				
				System.out.println(Global2.AIlevelP1);
				System.out.println(Global2.AIlevelP2);
				
			}

			else if (input.equalsIgnoreCase("HARD_V_HARD")) {
				Global2.AIlevelP1 = "HARD";
				Global2.AIlevelP2 = "HARD";
				System.out.print("HARD V HARD\n");
			}

			else if (input.equalsIgnoreCase("HARD_V_MEDIUM")) {
				Global2.AIlevelP1 = "HARD";
				Global2.AIlevelP2 = "MEDIUM";
				System.out.print("HARD V MEDIUM\n");
			}

			else if (input.equalsIgnoreCase("HARD_V_EASY")) {
				Global2.AIlevelP1 = "HARD";
				Global2.AIlevelP2 = "EASY";
				System.out.print("HARD V EASY\n");
			}

			else if (input.equalsIgnoreCase("MEDIUM_V_MEDIUM")) {
				Global2.AIlevelP1 = "MEDIUM";
				Global2.AIlevelP2 = "MEDIUM";
				System.out.print("MEDIUM V MEDIUM\n");
			}

			else if (input.equalsIgnoreCase("MEDIUM_V_EASY")) {
				Global2.AIlevelP1 = "MEDIUM";
				Global2.AIlevelP2 = "EASY";
				System.out.print("MEDIUM V EASY\n");
			}

			else if (input.equalsIgnoreCase("EASY_V_EASY")) {
				Global2.AIlevelP1 = "EASY";
				Global2.AIlevelP2 = "EASY";
				System.out.print("EASY V EASY\n");				
			}

			else if (input.equalsIgnoreCase("NO_AI")) {
				Global2.AIlevelP1 = "OFF";
				Global2.AIlevelP2 = "OFF";
				System.out.print("OK\n");
			}

			else if (input.equalsIgnoreCase("EASY")) {
				Square aiPlayer = ((PlayerIsAI(Square.player2)) ? (Square.player2)
						: (Square.player1));
				AIlevelSet(aiPlayer, "EASY");
				System.out.print("OK\n");
				
				System.out.println(Global2.AIlevelP1);
				System.out.println(Global2.AIlevelP2);
				
			}

			else if (input.equalsIgnoreCase("MEDIUM")) {
				Square aiPlayer = ((PlayerIsAI(Square.player2)) ? (Square.player2)
						: (Square.player1));
				AIlevelSet(aiPlayer,"MEDIUM");
				System.out.print("OK\n");
			}

			else if (input.equalsIgnoreCase("HARD")) {
				Square aiPlayer = ((PlayerIsAI(Square.player2)) ? (Square.player1)
						: (Square.player1));
				AIlevelSet(aiPlayer,"HARD");
				System.out.print("OK\n");
			} else if (input.charAt(0) == 'P') {
				if (input.charAt(1) == '1') {
					Global2.AIlevelP1 = input.substring(2);
					System.out.print("Ok\n");
				} else if (input.charAt(2) == '2') {
					Global2.AIlevelP2 = input.substring(2);
					System.out.print("OK\n");
				} else {
					System.out.print("ILLEGAL\n");
				}
			}
			// else if(input=="4X4"){
			else if (input.equalsIgnoreCase("4X4")) {
				System.out.print("OK\n");
				Global2.boardSize = 4;
			}

			// else if(input=="8X8"){
			else if (input.equalsIgnoreCase("8X8")) {
				System.out.print("OK\n");
				Global2.boardSize = 8;
			}
			// else if(input=="6"){
			else if (input.equalsIgnoreCase("6")) {
				Global2.AIlevelP1 = "MEDIUM";
				Global2.AIlevelP2 = "OFF";
				System.out.print("OK\n");
				System.out.print("P1:HARD-AI, P2:Human\n");
				Global2.displayOn = true;
				break;
			}
			// else if(input=="7"){
			else if (input.equalsIgnoreCase("7")) {
				Global2.AIlevelP2 = "MEDIUM";
				Global2.AIlevelP1 = "OFF";
				System.out.print("OK\n");
				System.out.print("P1:Human, P2:HARD-AI\n");
				Global2.displayOn = true;
				break;
			}
			// else if(input=="8"){
			else if (input.equalsIgnoreCase("8")) {
				Global2.AIlevelP1 = "MEDIUM";
				Global2.AIlevelP2 = "HARD";
				System.out.print("OK\n");
				System.out.print("P1:" + Global2.AIlevelP1 + "-AI, P2:"
						+ Global2.AIlevelP2 + "-AI\n");
				Global2.displayOn = true;
				break;
			}
			// else if(input=="88"){
			else if (input.equalsIgnoreCase("88")) {
				Global2.AIlevelP1 = "MEDIUM_DEBG";
				Global2.AIlevelP2 = "HARD";
				System.out.print("OK\n");
				System.out.print("P1: " + Global2.AIlevelP1 + "-AI, P2:"
						+ Global2.AIlevelP2 + "-AI\n");
				Global2.displayOn = true;
				break;
			} else {
				// if(input!="?"){std::cout<<"ILLEGAL\n";}
				if (!input.equalsIgnoreCase("?")) {
					System.out.print("ILLEGAL\n");
				}
				System.out
						.print("WHITE, BLACK, EASY, MEDIUM, HARD, DISPLAY_ON, EXIT\n");
			}
		}
		return 1;
	}

	// forward decleration
	// Pair findBestMoveZ(Square forecastPlayer,int depth);

	// forward decleration
	// Pair findBestMoveY(Square forecastPlayer,int depth);

	// forward decleration
	// Pair findBestMoveX(Square forecastPlayer,int depth);

	// receives all input during the execution of the game
	int handleGameInput(int client){
	int MoveCount=0,numOfGamesCompleted=0,blackWins=0,whiteWins=0;
    while(numOfGamesCompleted<Global2.totalExecutions){
		
        if(Global2.displayOn){			
            printReversi(Global2.game);
            
			System.out.print("-------------------\n");
			System.out.print("Move:" + MoveCount + "\n");
			System.out.print("Current Player:" + Global2.CurrentPlayer + "\n");
			if(PlayerIsAI(Global2.CurrentPlayer)) {
				System.out.print("Waiting on AI\n");
			} else {
				System.out.print("Human's Move\n");
			}	
        }
        
        //Check if Current Player can actually make a move 
        if (Global2.game.GetValidMoves(Global2.CurrentPlayer).isEmpty()==true){
			if(Global2.game.GetValidMoves(Global.GetOtherPlayer(Global2.CurrentPlayer)).isEmpty()==true){
				//no player has a move available - end game
				int n1 = Global2.game.Count(Square.player1);
				int n2 = Global2.game.Count(Square.player2);
				// TODO: ADD IN TIE CASE
				String p1Name = (PlayerIsAI(Square.player1))?(Global2.AIlevelP1+"-AI"):("Human");
				String p2Name = (PlayerIsAI(Square.player2))?(Global2.AIlevelP2+"-AI"):("Human");
				
				System.out.print("The game has ended after " + MoveCount + " moves!\n");
				System.out.print("Player1 (" + p1Name + ")["); printSquare(Square.player1); System.out.print("] conquered " + n1 + " squares.\n");
				System.out.print("Player2 (" + p2Name + ")["); printSquare(Square.player2); System.out.print("] conquered " + n2 + " squares.\n");
				
				if(n1==n2){
					PrintOut("It's a Draw!\n",client);		
				}
				else{			
					System.out.print("The winner is Player");
					if(n1>n2) {
						System.out.print("1(" + p1Name + ")");
					} else {
						System.out.print("2(" + p2Name + ")");
					}
					System.out.print("\nCongratulations!\n\n");
					
					if(n1>n2)
						blackWins++;
					else
						whiteWins++;
				
			}
				if(Global2.totalExecutions>1){
					System.out.print("Player1 (" + p1Name + ") won " + blackWins + "\n");
					System.out.print("Player2 (" + p2Name + ") won " + whiteWins + "\n");
				 numOfGamesCompleted++;
				 MoveCount=0;
				Global2.game.SetBoard(Global2.newBoard.GetBoard());
				 continue;
				}break;

			}else{
				//If Current Player cannot move, but other player can
				System.out.print("Too bad! Player"); printSquare(Global2.CurrentPlayer); System.out.print("("); printSquare(Global2.CurrentPlayer); System.out.print(") is unabled to do a valid move!\n");
				
				Global2.CurrentPlayer=Global.GetOtherPlayer(Global2.CurrentPlayer);
				System.out.print("The next turn goes to Player"); printSquare(Global2.CurrentPlayer); System.out.print("("); printSquare(Global2.CurrentPlayer); System.out.print(") !\n");
				System.out.print("Too bad! Player"); printSquare(Global2.CurrentPlayer); System.out.print("("); printSquare(Global2.CurrentPlayer); System.out.print(") is unabled to do a valid move!\n");
				System.out.print("The next turn again goes to Player"); printSquare(Global2.CurrentPlayer); System.out.print("("); printSquare(Global2.CurrentPlayer); System.out.print(") !\n");
				continue;
			}
        }
        MoveCount++;
        {//Input-Gathering Block
        	
            if(PlayerIsAI(Global2.CurrentPlayer)){
                //CurrentPlayer is an AI
                if(AIlevel(Global2.CurrentPlayer)=="EASY"){
                    moveRandomly();
                }
				/*else if(AIlevel(CurrentPlayer)=="MEDIUM"){
					AlphaBetaAI ai(game, CurrentPlayer, 3);
					coordinate = ai.findMax();
				}*/
               else if(AIlevel(Global2.CurrentPlayer)=="MEDIUM"){
					Pair bestMove = findBestMoveY(Global2.CurrentPlayer,0);
					Global2.coordinate.Set(bestMove.GetX(), bestMove.GetY());
               }

                else if(AIlevel(Global2.CurrentPlayer).substring(0,4)=="HARD"){
					Pair bestMove = findBestMoveX(Global2.CurrentPlayer,0);
					Global2.coordinate.Set(bestMove.GetX(), bestMove.GetY());
                }
				printSquare(Global2.CurrentPlayer);
                System.out.print("-AI Plays:"); System.out.print(Global2.coordinate.GetX()); System.out.print(Global2.coordinate.GetY());
				System.out.print('\n');			
            }
            else if( !(PlayerIsAI(Global2.CurrentPlayer)) ) {
                //CurrentPlayer is a Human
                System.out.print("::");
                String input = GetInput(client);
                boolean isValidCoordinate = IsCoordinate(input, Global2.coordinate); 
                if (isValidCoordinate == false){
                    //Input was not a Coordinate
                    //if(input=="EXIT") return 0;
					if( input.equalsIgnoreCase("EXIT")) return 0;
					// else if(input=="?"){
					else if( input.equalsIgnoreCase("?")) {
						System.out.print("Enter coordinates as # alpha values, DISPLAY_OFF, SHOW_NEXT_POS, UNDO, REDO, EXIT\n");
                        continue;
                    }
                    //else if(input=="DISPLAY_OFF"){
					else if( input.equalsIgnoreCase("DISPLAY_OFF")) {
                        Global2.displayOn = false;
                        System.out.print("OK\n");
                        continue;
                    }
                    //else if(input=="UNDO"){
					else if( input.equalsIgnoreCase("UNDO")) {
                        if(!Global2.game.DoUndo()){
							System.err.print("CANNOT UNDO, NOT ENOUGH STATES IN STACK\n");
							System.out.print("ILLEGAL\n");
                            continue;
                        }
                        continue;
                    }
                    //else if(input=="REDO"){
					else if( input.equalsIgnoreCase("REDO")) {
                        if(!Global2.game.DoRedo()){
                            System.err.print("CANNOT REDO, NOT ENOUGH STATES IN STACK\n");
                            System.out.print("ILLEGAL\n");
                            continue;
                        }
                        continue;
                    }
                    //else if(input=="SHOW_NEXT_POS"){
					else if( input.equalsIgnoreCase("SHOW_NEXT_POS")) {;
                        showPossibleMoves(client);
                        continue;
                    }
                    //else if(input=="RAND" || input=="R"){
					else if( input.equalsIgnoreCase("RAND") || input.equalsIgnoreCase("R")) {
                        moveRandomly();
                    }
                    else{
                        System.out.print("ILLEGAL\n");
                        continue;
                    }
                }
                if(isValidCoordinate){
                   //Valid coordinate was entered
                    if(Global2.displayOn){
                        System.out.print("Human Plays :" + Global2.coordinate.GetX() + Global2.coordinate.GetY() + '\n');
                      }
                }
            }//End Human Input
        }//End of Input-Gathering Block
        //Check that move is valid for Current Player.
        if (Global2.game.IsValidMove(Global2.coordinate.GetX(), Global2.coordinate.GetY(), Global2.CurrentPlayer) == false){
			System.out.print("ILLEGAL\n");
            continue; 
        }

        //Actually do the move 
        
        Global2.game.DoMove(Global2.coordinate.GetX(), Global2.coordinate.GetY(), Global2.CurrentPlayer);

        //Switch Player's turns
        Global2.CurrentPlayer=Global.GetOtherPlayer(Global2.CurrentPlayer); 
        if(Global2.displayOn){
        	System.out.print("-------------------\n\n");
        	}

    }
    return 1;
}

	// Handles user input and display of data
	int api(String commandLine,int client){
		if(client <= 0) {
			Global2.server = false;
		} else {
			Global2.server = true;
		}
		for(int i=0;i<40;i++) System.out.print('\n');
		System.out.print("WELCOME\n");
		while(true) {	
			
			if(handlePregameInput(client) == 0){return 0;}

			System.out.print("Player1: ");
			if(PlayerIsAI(Square.player1)) {
				System.out.print("AI	:" + Square.player1 + ": WHITE\n");
			} else {
				System.out.print("Human	:" + Square.player1 + ": WHITE\n");
			}
			
			System.out.print("Player2: ");
			if(PlayerIsAI(Square.player2)) {
				System.out.print("AI	:" + Square.player2 + ": BLACK\n");
			} else {
				System.out.print("Human :" + Square.player2 + ": BLACK\n");
			}		
			
			if(handleGameInput(client) == 0)
				return 0;
			System.out.print("Press ENTER to quit to continue.  Use the EXIT command a any time to quit.");			
			//std::cin.ignore(numeric_limits<streamsize>::max(), '\n');
			Global2.game.SetBoard(Global2.newBoard.GetBoard());
			return 0;
		}
		//return 0;
	}
	
	
	/**      HARD-AI      **/
	/* Developed by Curtis */

	//simply checks to see if the passed board is "game-over"
	int endGameEvaluator(GameReversi childBoard){
		Square opponent;
		if	(childBoard.Count(Square.empty) == 0||//No more empty squares or neither player has a move, end game 
			((childBoard.GetValidMoves(Square.player1).isEmpty()==true)&&
			(childBoard.GetValidMoves(Square.player2).isEmpty()==true))){ 
				if(Global2.CurrentPlayer==Square.player1)
					opponent=Square.player2;
				else
					opponent=Square.player1;
				if(childBoard.Count(Global2.CurrentPlayer)>childBoard.Count(opponent))
					return childBoard.Count(Global2.CurrentPlayer)*10000;
				else
					return childBoard.Count(opponent)*(-10000);
		}
		return 0;
	}

	//returns a positive weight if the move makes the opponent have to pass
	//or a negative weight if the opponent move would make you have to pass
	int numOfAvailableMovesEvaluator(GameReversi childBoard,Square forecastPlayer){
		//evaluating a board that forecastPlayer has just played on
		Square nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);
		if(childBoard.GetValidMoves(nextPlayer).isEmpty()){
			if(Global2.CurrentPlayer==forecastPlayer)
				return 10000;
			else
				return -10000;
		}
		return 0;

	}


	//Original-flawed
	int heuristicWeightZ(GameReversi childBoard, int x, int y, Square moveOwner, int depth){
		int cummulative=0;
		//got a corner
		if(	(x==0&&y==0)||//top left
			(x==Global2.boardSize-1&&y==Global2.boardSize-1)||//bottom right
			(x==0&&y==Global2.boardSize-1)||//bottom left
			(x==Global2.boardSize-1&&y==0)){ //top right
			if(moveOwner==Global2.CurrentPlayer)
				return  5000;//player wants this bad
			else
				return -5000;//player definitely doesn't want opponent to have
		}
		//edge next to empty corner
		if( (((x==0&&y==1)||(x==1&&y==0))&&childBoard.GetSquare(0,0)==Square.empty)||
			(((x==0&&y==6)||(x==1&&y==7))&&childBoard.GetSquare(0,7)==Square.empty)||
			(((x==6&&y==0)||(x==7&&y==1))&&childBoard.GetSquare(7,0)==Square.empty)||
			(((x==6&&y==7)||(x==1&&y==6))&&childBoard.GetSquare(7,7)==Square.empty)){
				if(moveOwner==Global2.CurrentPlayer)
					return -1500;
				else
					return 1500;
		}
		//got an edge
		if( x==0||//left edge
			x==Global2.boardSize-1||//right edge
			y==0||//top edge
			y==Global2.boardSize-1){//bottom edge
			if(moveOwner==Global2.CurrentPlayer)
				return 1500;
			else
				return -1500;
		}
		//avoid these
		if( x==1||x==Global2.boardSize-2)
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= -1000;//in this case player doesn't want this move
			else
				cummulative+= 1000;//player would like his opponent to make this move
		if( y==1||y==Global2.boardSize-2)
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= -1000;//in this case player doesn't want this move
			else
				cummulative+= 1000;//player would like his opponent to make this move

		return cummulative;
	}

	//uses a health-number for the different parts of the board to determine a moves strength
	int heuristicWeightY(GameReversi childBoard, int x, int y, Square moveOwner, int depth){
		int cummulative=0;
		//got a corner
		if(	(x==0&&y==0)||//top left
			(x==Global2.boardSize-1&&y==Global2.boardSize-1)||//bottom right
			(x==0&&y==Global2.boardSize-1)||//bottom left
			(x==Global2.boardSize-1&&y==0)){ //top right
			if(moveOwner==Global2.CurrentPlayer)
				return  10000/(depth+1);//45,19@5000&10k&11k&15k
			else
				return -10000/(depth+1);//45,19@-5000&-10k&-11k&-15k
		}
		//edge next to empty corner
		if( (((x==0&&y==1)||(x==1&&y==0))&&childBoard.GetSquare(0,0)==Square.empty)||
			(((x==0&&y==6)||(x==1&&y==7))&&childBoard.GetSquare(0,7)==Square.empty)||
			(((x==6&&y==0)||(x==7&&y==1))&&childBoard.GetSquare(7,0)==Square.empty)||
			(((x==6&&y==7)||(x==1&&y==6))&&childBoard.GetSquare(7,7)==Square.empty)){
				if(moveOwner==Global2.CurrentPlayer)
					return -5000/(depth+1);//36,28@-3000/33,31@5000&6000/39,25@-7000
				else
					return 5000/(depth+1);//36,28@3000/33,31@5000&6000/39,25@7000
		}
		//safe edge next to corner
		if( (((x==0&&y==1)||(x==1&&y==0))&&childBoard.GetSquare(0,0)==moveOwner)||
			(((x==0&&y==6)||(x==1&&y==7))&&childBoard.GetSquare(0,7)==moveOwner)||
			(((x==6&&y==0)||(x==7&&y==1))&&childBoard.GetSquare(7,0)==moveOwner)||
			(((x==6&&y==7)||(x==7&&y==6))&&childBoard.GetSquare(7,7)==moveOwner)){
				if(moveOwner==Global2.CurrentPlayer)
					return 5000/(depth+1);//36,28@-3000/33,31@5000&6000/39,25@-7000
				else
					return -5000/(depth+1);//36,28@3000/33,31@5000&6000/39,25@7000
		}
		//sweet-sixteen corner
		if( ((x==2&&y==2)&&childBoard.GetSquare(0,0)==Square.empty)||
			((x==2&&y==5)&&childBoard.GetSquare(0,7)==Square.empty)||
			((x==5&&y==2)&&childBoard.GetSquare(7,0)==Square.empty)||
			((x==5&&y==5)&&childBoard.GetSquare(7,7)==Square.empty) ){
				if(moveOwner==Global2.CurrentPlayer)
					return 2500/(depth+1);
				else
					return -2500/(depth+1);
		}
		//deadman's corner
		if( ((x==1&&y==1)&&childBoard.GetSquare(0,0)==Square.empty)||
			((x==1&&y==6)&&childBoard.GetSquare(0,7)==Square.empty)||
			((x==6&&y==1)&&childBoard.GetSquare(7,0)==Square.empty)||
			((x==6&&y==6)&&childBoard.GetSquare(7,7)==Square.empty) ){
				if(moveOwner==Global2.CurrentPlayer)
					return -5000/(depth+1);
				else
					return 5000/(depth+1);
		}
		//got an edge, blocking a straightaway
		if( (x==0&&(y<Global2.boardSize-3&&y>2))&&
			childBoard.GetSquare(x+1,y)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( (x==Global2.boardSize-1&&(y<Global2.boardSize-3&&y>2))&&
			childBoard.GetSquare(x-1,y)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( (y==0&&(x<Global2.boardSize-3&&x>2))&&
			childBoard.GetSquare(x,y+1)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( (y==Global2.boardSize-1&&(x<Global2.boardSize-3&&x>2))&&
			childBoard.GetSquare(x,y-1)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( x==0||//left edge
			x==Global2.boardSize-1||//right edge
			y==0||//top edge
			y==Global2.boardSize-1){//bottom edge
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 750/(depth+1);
			else
				cummulative-= 750/(depth+1);
		}
		//avoid these
		if( x==1||x==Global2.boardSize-2){
			if(moveOwner==Global2.CurrentPlayer)
				cummulative-= 1500/(depth+1);//in this case player doesn't want this move
			else
				cummulative+= 1500/(depth+1);//player would like his opponent to make this move
		}
		if( y==1||y==Global2.boardSize-2){
			if(moveOwner==Global2.CurrentPlayer)
				cummulative-= 1500/(depth+1);//in this case player doesn't want this move
			else
				cummulative+= 1500/(depth+1);//player would like his opponent to make this move
		}

		return cummulative;
	}

	//uses a health-number for the different parts of the board to determine a moves strength
	int heuristicWeightX(GameReversi childBoard, int x, int y, Square moveOwner, int depth){
		int cummulative=0;
		//got a corner
		if(	(x==0&&y==0)||//top left
			(x==Global2.boardSize-1&&y==Global2.boardSize-1)||//bottom right
			(x==0&&y==Global2.boardSize-1)||//bottom left
			(x==Global2.boardSize-1&&y==0)){ //top right
			if(moveOwner==Global2.CurrentPlayer)
				return  10000/(depth+1);//45,19@5000&10k&11k&15k
			else
				return -10000/(depth+1);//45,19@-5000&-10k&-11k&-15k
		}
		//edge next to empty corner
		if( (((x==0&&y==1)||(x==1&&y==0))&&childBoard.GetSquare(0,0)==Square.empty)||
			(((x==0&&y==6)||(x==1&&y==7))&&childBoard.GetSquare(0,7)==Square.empty)||
			(((x==6&&y==0)||(x==7&&y==1))&&childBoard.GetSquare(7,0)==Square.empty)||
			(((x==6&&y==7)||(x==1&&y==6))&&childBoard.GetSquare(7,7)==Square.empty)){
				if(moveOwner==Global2.CurrentPlayer)
					return -5000/(depth+1);//36,28@-3000/33,31@5000&6000/39,25@-7000
				else
					return 5000/(depth+1);//36,28@3000/33,31@5000&6000/39,25@7000
		}
		//safe edge next to corner
		if( (((x==0&&y==1)||(x==1&&y==0))&&childBoard.GetSquare(0,0)==moveOwner)||
			(((x==0&&y==6)||(x==1&&y==7))&&childBoard.GetSquare(0,7)==moveOwner)||
			(((x==6&&y==0)||(x==7&&y==1))&&childBoard.GetSquare(7,0)==moveOwner)||
			(((x==6&&y==7)||(x==7&&y==6))&&childBoard.GetSquare(7,7)==moveOwner)){
				if(moveOwner==Global2.CurrentPlayer)
					return 5000/(depth+1);//36,28@-3000/33,31@5000&6000/39,25@-7000
				else
					return -5000/(depth+1);//36,28@3000/33,31@5000&6000/39,25@7000
		}
		//safe edge next to safe edge next to corner
		if( ((x==0&&y==2)	&&childBoard.GetSquare(0,1)==moveOwner
							&&childBoard.GetSquare(0,0)==moveOwner)||
			((x==0&&y==5)	&&childBoard.GetSquare(0,6)==moveOwner
							&&childBoard.GetSquare(0,7)==moveOwner)||
			((x==7&&y==2)	&&childBoard.GetSquare(7,1)==moveOwner
							&&childBoard.GetSquare(7,0)==moveOwner)||
			((x==7&&y==5)	&&childBoard.GetSquare(7,6)==moveOwner
							&&childBoard.GetSquare(7,7)==moveOwner)||
			((x==5&&y==0)	&&childBoard.GetSquare(6,0)==moveOwner
							&&childBoard.GetSquare(7,0)==moveOwner)||
			((x==5&&y==7)	&&childBoard.GetSquare(6,7)==moveOwner
							&&childBoard.GetSquare(7,7)==moveOwner)||
			((x==2&&y==0)	&&childBoard.GetSquare(1,0)==moveOwner
							&&childBoard.GetSquare(0,0)==moveOwner)||
			((x==2&&y==7)	&&childBoard.GetSquare(1,7)==moveOwner
							&&childBoard.GetSquare(0,7)==moveOwner)){
				if(moveOwner==Global2.CurrentPlayer)
					return 5000/(depth+1);//was 43,21 still 43,21
				else
					return -5000/(depth+1);//
		}
		//sweet-sixteen corner
		if( ((x==2&&y==2)&&childBoard.GetSquare(0,0)==Square.empty)||
			((x==2&&y==5)&&childBoard.GetSquare(0,7)==Square.empty)||
			((x==5&&y==2)&&childBoard.GetSquare(7,0)==Square.empty)||
			((x==5&&y==5)&&childBoard.GetSquare(7,7)==Square.empty) ){
				if(moveOwner==Global2.CurrentPlayer)
					return 2500/(depth+1);
				else
					return -2500/(depth+1);
		}
		//deadman's corner
		if( ((x==1&&y==1)&&childBoard.GetSquare(0,0)==Square.empty)||
			((x==1&&y==6)&&childBoard.GetSquare(0,7)==Square.empty)||
			((x==6&&y==1)&&childBoard.GetSquare(7,0)==Square.empty)||
			((x==6&&y==6)&&childBoard.GetSquare(7,7)==Square.empty) ){
				if(moveOwner==Global2.CurrentPlayer)
					return -5000/(depth+1);
				else
					return 5000/(depth+1);
		}
		//got an edge, blocking a straightaway
		if( (x==0&&(y<Global2.boardSize-3&&y>2))&&
			childBoard.GetSquare(x+1,y)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( (x==Global2.boardSize-1&&(y<Global2.boardSize-3&&y>2))&&
			childBoard.GetSquare(x-1,y)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( (y==0&&(x<Global2.boardSize-3&&x>2))&&
			childBoard.GetSquare(x,y+1)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( (y==Global2.boardSize-1&&(x<Global2.boardSize-3&&x>2))&&
			childBoard.GetSquare(x,y-1)==Global.GetOtherPlayer(moveOwner))
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 1500/(depth+1);
			else
				cummulative+= -1500/(depth+1);
		if( x==0||//left edge
			x==Global2.boardSize-1||//right edge
			y==0||//top edge
			y==Global2.boardSize-1){//bottom edge
			if(moveOwner==Global2.CurrentPlayer)
				cummulative+= 750/(depth+1);
			else
				cummulative-= 750/(depth+1);
		}
		//avoid these
		if( x==1||x==Global2.boardSize-2){
			if(moveOwner==Global2.CurrentPlayer)
				cummulative-= 1500/(depth+1);//in this case player doesn't want this move
			else
				cummulative+= 1500/(depth+1);//player would like his opponent to make this move
		}
		if( y==1||y==Global2.boardSize-2){
			if(moveOwner==Global2.CurrentPlayer)
				cummulative-= 1500/(depth+1);//in this case player doesn't want this move
			else
				cummulative+= 1500/(depth+1);//player would like his opponent to make this move
		}

		return cummulative;
	}

	//Original-flawed
	int checkForWeightZ(GameReversi parentBoard, Square forecastPlayer,int depth){
		//MaxMoveWeight is private to this branch of the tree
		//but publicly used to the childBoards (leaf-nodes)
		int MaxMoveWeight=-99999;

		Vector<Pair>vals=parentBoard.GetValidMoves(forecastPlayer);

		//here we itterate through the moves, creating a private object for each
		//move that contains some private variables and a private boardState that
		//records the addition of the new move to the parentBoard that was passed in
		for(int possibleMove=0;possibleMove<vals.size();possibleMove++){
			int forecastedMoveWeight=0;
			Square nextPlayer;
			//this childBoard is private to this particular move
			//and will record this move as a new state of the board
			//to be passed recursively for examination of new moves
			//that will be available after this move.
			GameReversi childBoard = new GameReversi(Global2.boardSize);				
			childBoard.SetBoard(parentBoard.GetBoard());

			//evaluate and assign a weight to this move
			//forecastedMoveWeights is private to this move
			//and will be replaced when we itterate to the next
			//move of this stage
			forecastedMoveWeight+=heuristicWeightZ(	childBoard, vals.elementAt(possibleMove).GetX(), vals.elementAt(possibleMove).GetY(),
													forecastPlayer, depth);

			//now add the move to this private board
			childBoard.DoMove(	vals.elementAt(possibleMove).GetX(),
								vals.elementAt(possibleMove).GetY(),
								forecastPlayer);
			//we will pass a copy of this board recursively
			//here, we attempt to only recurse if necessary 
			if(	depth<Global2.maxDepthZ&&//if not at our maximum allowed recursion
				heuristicWeightZ(childBoard,//here we say that we don't want to bother
								vals.elementAt(possibleMove).GetX(),//checking further down this move's
								vals.elementAt(possibleMove).GetY(),//lineage if it involves the
								forecastPlayer, depth)!=-10000){//opponent taking a corner 
					nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);
					forecastedMoveWeight+=checkForWeightZ(childBoard,nextPlayer,depth+1);
			}
	/*		else{
				return childBoard.Count(CurrentPlayer)*100+endGameEvaluator(childBoard);
			}*///found the final board at this depth is not evaluated for each possible move

			//keep track of which move is the best at this stage of the branch
			//MaxMoveWeight is private to this stage of the branch
			//and will be returned as the maximum possible move weight
			if(forecastedMoveWeight>MaxMoveWeight)
				MaxMoveWeight=forecastedMoveWeight;	
			//stop itterating through the for loop looking at the next available
			//move, if we have already found a great move (alpha-beta, pruning)
			//we use a different acceptable weight at the beginning of the game.
			if(childBoard.Count(Square.empty)>53){
				if(MaxMoveWeight>500*Global2.maxDepthZ)
					break;
			}else if(MaxMoveWeight>900*Global2.maxDepthZ)
				break;
			
		}
		return MaxMoveWeight+vals.size();//decided to add the num of avail
	}

	//recursively builds a tree of moves and then uses heuristicWeightZ to 
	//build a chain of weights that can be summed to findBestMove
	int checkForWeightY(GameReversi parentBoard, Square forecastPlayer,int depth){
		//MaxMoveWeight is private to this branch of the tree
		//but publicly used to the childBoards (leaf-nodes)
		int MaxMoveWeight=-99999;

		Vector<Pair>vals=parentBoard.GetValidMoves(forecastPlayer);
		//Vector<Integer> weights;	
//		if(!endGameEvaluator(parentBoard))
//			return endGameEvaluator(parentBoard);
		if(endGameEvaluator(parentBoard)!=0)
				return endGameEvaluator(parentBoard);

		//here we iterate through the moves, creating a private object for each
		//move that contains some private variables and a private boardState that
		//records the addition of the new move to the parentBoard that was passed in
		for(int possibleMove=0;possibleMove<vals.size();possibleMove++){
			int forecastedMoveWeight=0;
			Square nextPlayer;

			//this childBoard is private to this particular move
			//and will record this move as a new state of the board
			//to be passed recursively for examination of new moves
			//that will be available after this move.
			GameReversi childBoard = new GameReversi(Global2.boardSize);				
			childBoard.SetBoard(parentBoard.GetBoard());

			//evaluate and assign a weight to this move
			//forecastedMoveWeights is private to this move
			//and will be replaced when we iterate to the next
			//move of this stage
			forecastedMoveWeight+=heuristicWeightY(	childBoard,
													vals.elementAt(possibleMove).GetX(),
													vals.elementAt(possibleMove).GetY(),
													forecastPlayer, depth);

			//now add the move to this private board
			childBoard.DoMove(	vals.elementAt(possibleMove).GetX(),
								vals.elementAt(possibleMove).GetY(),
								forecastPlayer);
			if(numOfAvailableMovesEvaluator(childBoard,forecastPlayer)!=0)
				forecastedMoveWeight+=numOfAvailableMovesEvaluator(childBoard,forecastPlayer);
			//we will pass a copy of this board recursively
			//here, we attempt to only recurse if necessary 
			if(	depth<Global2.maxDepthY&&//if not at our maximum allowed recursion
				heuristicWeightY(childBoard,//here we say that we don't want to bother
								vals.elementAt(possibleMove).GetX(),//checking further down this move's
								vals.elementAt(possibleMove).GetY(),//lineage if it involves the
								forecastPlayer, depth)!=-10000/(depth+1)){//opponent taking a corner 
					nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);
					forecastedMoveWeight+=checkForWeightY(childBoard,nextPlayer,depth+1);

						
			}
	/*		else{
				return childBoard.Count(CurrentPlayer)*100+endGameEvaluator(childBoard);
			}*///found the final board at this depth is not evaluated for each possible move

			//keep track of which move is the best at this stage of the branch
			//MaxMoveWeight is private to this stage of the branch
			//and will be returned as the maximum possible move weight
			if(forecastedMoveWeight>MaxMoveWeight)
				MaxMoveWeight=forecastedMoveWeight;	
			//stop iterating through the for loop looking at the next available
			//move, if we have already found a great move (alpha-beta, pruning)
			//we use a different acceptable weight at the beginning of the game.
			if(childBoard.Count(Square.empty)>53){
				if(MaxMoveWeight>500*Global2.maxDepthY)
					break;
			}else if(MaxMoveWeight>900*Global2.maxDepthY)
				break;
	/*		weights.push_back(forecastedMoveWeight);
			if(weights.size()==vals.size())
				std::cout<<possibleMove+1;*/
			
		}
	/*	if(vals.size()==0&&parentBoard.Count(empty)>4)
			return MaxMoveWeight;
		else if(vals.size()==0&&parentBoard.Count(empty)>0)
			return -10000;
		else*/
			return MaxMoveWeight+vals.size()*100;//decided to add the num of avail
	}

	//experimental version
	int checkForWeightX(GameReversi parentBoard, Square forecastPlayer,int depth){
		//MaxMoveWeight is private to this branch of the tree
		//but publicly used to the childBoards (leaf-nodes)
		int MaxMoveWeight=-99999;

		Vector<Pair> vals = parentBoard.GetValidMoves(forecastPlayer);
		Vector<Integer> weights;	
//		if(!endGameEvaluator(parentBoard))
//			return endGameEvaluator(parentBoard);
		if(endGameEvaluator(parentBoard)!=0)
				return endGameEvaluator(parentBoard);

		//here we iterate through the moves, creating a private object for each
		//move that contains some private variables and a private boardState that
		//records the addition of the new move to the parentBoard that was passed in
		for(int possibleMove=0;possibleMove<vals.size();possibleMove++){
			int forecastedMoveWeight=0;
			Square nextPlayer;

			//this childBoard is private to this particular move
			//and will record this move as a new state of the board
			//to be passed recursively for examination of new moves
			//that will be available after this move.
			GameReversi childBoard = new GameReversi(Global2.boardSize);				
			childBoard.SetBoard(parentBoard.GetBoard());

			//evaluate and assign a weight to this move
			//forecastedMoveWeights is private to this move
			//and will be replaced when we iterate to the next
			//move of this stage
			forecastedMoveWeight+=heuristicWeightX(	childBoard,
													vals.elementAt(possibleMove).GetX(),
													vals.elementAt(possibleMove).GetY(),
													forecastPlayer, depth);

			//now add the move to this private board
			childBoard.DoMove(	vals.elementAt(possibleMove).GetX(),
								vals.elementAt(possibleMove).GetY(),
								forecastPlayer);
			if(numOfAvailableMovesEvaluator(childBoard,forecastPlayer)!=0)
				forecastedMoveWeight+=numOfAvailableMovesEvaluator(childBoard,forecastPlayer);
			//we will pass a copy of this board recursively
			//here, we attempt to only recurse if necessary 
			if(	depth<Global2.maxDepthX&&//if not at our maximum allowed recursion
				heuristicWeightX(childBoard,//here we say that we don't want to bother
								vals.elementAt(possibleMove).GetX(),//checking further down this move's
								vals.elementAt(possibleMove).GetY(),//lineage if it involves the
								forecastPlayer, depth)!=-10000/(depth+1)){//opponent taking a corner 
					nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);
					forecastedMoveWeight+=checkForWeightX(childBoard,nextPlayer,depth+1);

						
			}
			else
				forecastedMoveWeight+=	(childBoard.Count(Global2.CurrentPlayer)
										-childBoard.Count(Global.GetOtherPlayer(Global2.CurrentPlayer)))
										*depth;//went from 30,34 to 43,21
			//keep track of which move is the best at this stage of the branch
			//MaxMoveWeight is private to this stage of the branch
			//and will be returned as the maximum possible move weight
			if(forecastedMoveWeight>MaxMoveWeight)
				MaxMoveWeight=forecastedMoveWeight;	
			//stop itterating through the for loop looking at the next available
			//move, if we have already found a great move (alpha-beta, pruning)
			//we use a different acceptable weight at the beginning of the game.
			if(childBoard.Count(Square.empty)>50){
				if(MaxMoveWeight>300*Global2.maxDepthX)
					break;
			}else if(childBoard.Count(Square.empty)>25){
				if(MaxMoveWeight>500*Global2.maxDepthX)
					break;
			}else
				if(MaxMoveWeight>600*Global2.maxDepthX)
					break;
	/*		weights.push_back(forecastedMoveWeight);
			if(weights.size()==vals.size())
				std::cout<<possibleMove+1;*/
			
		}
	/*	if(vals.size()==0&&parentBoard.Count(empty)>4)
			return MaxMoveWeight;
		else if(vals.size()==0&&parentBoard.Count(empty)>0)
			return -10000;
		else*/
			return MaxMoveWeight+vals.size()*100;//decided to add the num of avail
	}

	//Original-flawed
	Pair findBestMoveZ(Square forecastPlayer,int depth){
		int maxMoveWeight=-99999,immediatePlusForecastWeights=0,maxPossibleMove=0;
		Square nextPlayer;
		//vals will hold the possible moves for the player we want to forecast at this depth
		Vector<Pair>vals=Global2.game.GetValidMoves(forecastPlayer);
		for(int possibleMove=0;possibleMove<vals.size();possibleMove++){	
			int immediateMoveWeights=0;
			GameReversi boardForAPrimaryMove = new GameReversi(Global2.boardSize);
			//new board (branch to tree) for each possibleMove created each itteration
			boardForAPrimaryMove.SetBoard(Global2.game.GetBoard());
			//add this possible move to its board
			boardForAPrimaryMove.DoMove(vals.elementAt(possibleMove).GetX(),
										vals.elementAt(possibleMove).GetY(),
										forecastPlayer);
			immediateMoveWeights+=heuristicWeightZ(	boardForAPrimaryMove,
											vals.elementAt(possibleMove).GetX(),
											vals.elementAt(possibleMove).GetY(),
											forecastPlayer, depth);
			nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);//figure out who is the next player
			immediatePlusForecastWeights+=checkForWeightZ(boardForAPrimaryMove,nextPlayer,1);//the maximum outcome from the set Depth (checkForWeightZ())
			if(immediatePlusForecastWeights>maxMoveWeight){	//if this possible Move's maximum outcome is bigger
				maxMoveWeight=immediatePlusForecastWeights;	//keep it as the new maximum overall
				maxPossibleMove=possibleMove;	//and remember which move that maximum belongs to
			}
		}
		System.out.print("Value of this move:" + maxMoveWeight + '\n');
		return vals.elementAt(maxPossibleMove);//when done checking down each branch of possible Moves send the best move back

	}

	//takes the game board and creates a tree of moves, passing a game board with
	//each immediately available move, to checkForWeightz
	Pair findBestMoveY(Square forecastPlayer,int depth){
		//Random(time(NULL));
		//Random(System.currentTimeMillis());
		int maxMoveWeight=-99999,immediatePlusForecastWeights=0,maxPossibleMove=0;
		Square nextPlayer;
		Vector<Pair>movesTiedForTop;
		Vector<Integer> weights = new Vector<Integer>();
		//vals will hold the possible moves for the player we want to forcast at this depth
		Vector<Pair>vals=Global2.game.GetValidMoves(forecastPlayer);
		for(int possibleMove=0;possibleMove<vals.size();possibleMove++){	
			int immediateMoveWeights=0;
			GameReversi boardForAPrimaryMove = new GameReversi(Global2.boardSize);
			//new board (branch to tree) for each possibleMove created each itteration
			boardForAPrimaryMove.SetBoard(Global2.game.GetBoard());
			//add this possible move to its board
			boardForAPrimaryMove.DoMove(vals.elementAt(possibleMove).GetX(),
										vals.elementAt(possibleMove).GetY(),
										forecastPlayer);
			immediateMoveWeights+=heuristicWeightY(	boardForAPrimaryMove,
											vals.elementAt(possibleMove).GetX(),
											vals.elementAt(possibleMove).GetY(),
											forecastPlayer, depth);
			nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);//figure out who is the next player
			immediateMoveWeights+=checkForWeightY(boardForAPrimaryMove,nextPlayer,1);//the maximum outcome from the set Depth (checkForWeightZ())
			if(immediateMoveWeights>maxMoveWeight){	//if this possible Move's maximum outcome is bigger
//				if(movesTiedForTop.size()>0)
//					movesTiedForTop.clear();
//				movesTiedForTop.push_back(vals.elementAt(possibleMove));
				maxMoveWeight=immediateMoveWeights;	//keep it as the new maximum overall
				maxPossibleMove=possibleMove;	//and remember which move that maximum belongs to
			}//else if(immediatePlusForecastWeights==maxMoveWeight){
//				movesTiedForTop.push_back(vals.elementAt(possibleMove));
//			}
			weights.add(immediateMoveWeights);
		}
		//std::cout<<"Value of this move:"<<maxMoveWeight<<std::endl;
//		int randomOfTheTopMoves = rand() % movesTiedForTop.size();
		return vals.elementAt(maxPossibleMove);//when done checking down each branch of possible Moves send the best move back

	}

	//experimental version
	Pair findBestMoveX(Square forecastPlayer,int depth){
		//srand ( time(NULL) );
		int maxMoveWeight=-99999,immediatePlusForecastWeights=0,maxPossibleMove=0;
		Square nextPlayer;
		Vector<Pair> movesTiedForTop;
		Vector<Integer> weights = new Vector<Integer>();
		//vals will hold the possible moves for the player we want to forecast at this depth
		Vector<Pair> vals=Global2.game.GetValidMoves(forecastPlayer);
		for(int possibleMove=0;possibleMove<vals.size();possibleMove++){	
			int immediateMoveWeights=0;
			GameReversi boardForAPrimaryMove = new GameReversi(Global2.boardSize);
			//new board (branch to tree) for each possibleMove created each iteration
			boardForAPrimaryMove.SetBoard(Global2.game.GetBoard());
			//add this possible move to its board
			boardForAPrimaryMove.DoMove(vals.elementAt(possibleMove).GetX(),
										vals.elementAt(possibleMove).GetY(),
										forecastPlayer);
			immediateMoveWeights+=heuristicWeightX(	boardForAPrimaryMove,
											vals.elementAt(possibleMove).GetX(),
											vals.elementAt(possibleMove).GetY(),
											forecastPlayer, depth);
			nextPlayer = (forecastPlayer == Square.player1 ? Square.player2 : Square.player1);//figure out who is the next player
			immediateMoveWeights+=checkForWeightX(boardForAPrimaryMove,nextPlayer,1);//the maximum outcome from the set Depth (checkForWeightZ())
			if(immediateMoveWeights>maxMoveWeight){	//if this possible Move's maximum outcome is bigger
				maxMoveWeight=immediateMoveWeights;	//keep it as the new maximum overall
				maxPossibleMove=possibleMove;	//and remember which move that maximum belongs to
			}//else if(immediatePlusForecastWeights==maxMoveWeight){
//				movesTiedForTop.push_back(vals.elementAt(possibleMove));
//			}
			weights.add(immediateMoveWeights);
		}
		//std::cout<<"Value of this move:"<<maxMoveWeight<<std::endl;
//		int randomOfTheTopMoves = rand() % movesTiedForTop.size();
		return vals.elementAt(maxPossibleMove);//when done checking down each branch of possible Moves send the best move back
	}
};
