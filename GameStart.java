
class Global2 {
	
	//Global Variables //Default case, P1=Black=Human, P2=WHITE=EASYAI. //P1 ALWAYS goes first
	static int boardSize=8,randomMove,maxDepth=4,maxDepthX=6,maxDepthY=4,maxDepthZ=4,totalExecutions=1,blackWins=0,whiteWins=0,alphaBeta=-9999;
	static boolean displayOn=true,test=false,server=false;
	static Square CurrentPlayer=Square.player1; //Indicates whose turn it currently is. Game always starts with P1, who is always BLACK.
	static String defaultAISetting="EASY";
	static String AIlevelP1 = "OFF"; //Defaults P2 'OFF' i.e. P2==Human.
	static String AIlevelP2 = defaultAISetting; ////Defaults P2 to EASY AI.   EASY, MEDIUM, HARD, HARDdebg, ... { P1EASY, P2HARD, P2HARD3,...}(commands. ie to make player2 a hard level AI that looks ahead 3 plys)
	static String NullString="NULL";
	static Pair coordinate,bestMove;	
	
	GameReversi newBoard = new GameReversi(boardSize);
	GameReversi game = new GameReversi(boardSize);	
	
}

class gameStart {

public

//used to determine if the passed player is one of the AIs
boolean PlayerIsAI(Square Player){
    if(Player==Square.player1){return (Global2.AIlevelP1!="OFF"); }
    else if(Player==Square.player2){return (Global2.AIlevelP2!="OFF");}
    else{
        //std::cerr<<"Error: Player passed into --bool isPlayerAI(Square Player)-- must be either player1 or player2.\n";
        return false;
    }
} 
boolean isPlayerAI(Square Player){return PlayerIsAI(Player);}

//returns the name of the AI as a string, EASY, MEDIUM, HARD
String AIlevel(Square Player){ // Getter/Setter  i.e. void MakeP1Human(){ AIlevel(player1)="OFF";}
    if(!isPlayerAI(Player)){std::cerr<<"Error: Player passed into --std::string& AIlevel(Square Player)-- is not AI.\n"; return Global2.NullString;}
    if(Player == Square.player1){return Global2.AIlevelP1;}
    else if(Player == Square.player2){return Global2.AIlevelP2;}
    else{ 
    	//std::cerr<<"Error: Player passed into --std::string& AIlevel(Square Player)-- must be either player1 or player2.\n";
    	return Global2.NullString;
    }
}

//<< override for Reversi object squares
std::ostream& operator<<(std::ostream& os, const Square s) { 
    switch(s){
        case empty  : os <<GREEN<<"_"; break; 
        case validMove: os <<BOLDYELLOW<<"x"<<RESET<<GREEN; break;
        case player1: os <<BOLDBLACK<<"O"<<RESET<<GREEN; break; //char(2); break; 
        case player2: os <<BOLDWHITE<<"@"<<RESET<<GREEN; break; //char(1); break; 
        default: assert(!"Should not get here"); break; 
    }
    return os; 
}

//<< override for Reversi object board outlay
std::ostream& operator<<(std::ostream& os, const Reversi& r) { 
  { //Show the indices horizontally 
    const int size = r.GetSize(); 
    os << "  "; 
    for (int i=0; i!=size; ++i) 
    { 
        os<<GREEN<<"_ "<<RESET;
      //os << (i%10)<<" "; 
    } 
    os << std::endl; 
  } 
  { 
    int i = 0; 
    const std::vector<std::vector<Square> >& board = r.GetBoard(); 
    const std::vector<std::vector<Square> >::const_iterator lastRow = board.end(); 
    for (std::vector<std::vector<Square> >::const_iterator row = board.begin(); 
      row!=lastRow; 
      ++row, ++i) 
    { 
      os << WHITE<<(i+1) <<GREEN<<"|"<<RESET; 
      std::copy( (*row).begin(), (*row).end(),std::ostream_iterator<Square>(os,"|")); 
      os<<"\n"<<RESET;
      //os << " " << (i%10) << '\n'; 
    } 
  } 

  { //Show the indices horizontally 
    //os << std::endl; 
    os << "  "; 
    const int size = r.GetSize(); 
    for (int i=0; i!=size; ++i) 
    { 
      os<<WHITE<< char( (int('a')+i) )<<RESET<< " ";
      //os << i%10; 
    } 
    os << std::endl; 
  }
  return os; 
} 

//Returns true if the string can be converted to integers
boolean IsInt(String s, int rInt){ 
  //istringstream i(s);
  try {
	  int x = Integer.parseInt(s);
	  return true;
  }
  catch(NumberFormatException nFE) {
	  return false;
  }
} 

//Handles all input and drops the newline char.  Either recv or getlines.
String GetInput(int client) {
	if(client != 0) {
		char input[30];
		std::stringstream ss;
		recv(client, input, 30, 0);
	
		for(int i = 0; input[i] != '\n'; i++) {
			ss<<(toupper(input[i]));
		}
	
		return ss.str();
	} else {
		string input;
		std::cin >> input;
		for(int i=0; i<input.size(); i++){
			input[i] = toupper(input[i]);
		}
		return input;
	}
	
}

//Handles all output.  Takes a string and either cout or sends
void PrintOut(String inString,int client){

	char charToSend = new char[inString.size()];
	//char charToSend[80];
	strcpy(charToSend,inString.c_str());
	if(server==true){
		//send(client,charToSend+'\n',sizeof(charToSend+'\n'),0);
	}else {
		cout<<inString;
	}

	delete [] charToSend;
}

//Breaks up the coordinate input for an x and y value
public Vector<String> SeperateString(String input, char seperator){
  assert(input.empty()==false); 
  assert(input[0]!=seperator); 
  assert(input[input.size()-1]!=seperator); 

  Vector<String> result; 
  int pos = 0; 
  while(pos<static_cast<integer>(input.size())) 
  { 
    if (input[pos]==seperator) 
    { 
      String found = input.substr(0,pos); 
      result.push_back(found); 
      input = input.substr(pos+1,input.size()-pos); 
      pos = 0; 
    } 
    ++pos; 
  } 
  result.push_back(input); 
  return result; 
}

//Up front determination whether the users coordinate is a valid input type 
boolean IsCoordinate(String input, Pair coordinate){
    if((input.size()!=3 && server) || (input.size()!=2 && !server)) return false;
	//TODO: need error (bounds) checking on x and y
    int x, y;
		x = input[0] - 'A';
		y = integer(input[1] - '0')-1;
	if((x==0||(x>0&&x<8))&&(y==0||(y>0&&y<8))){

		cout<<"coordinate "<<x<<" and "<<y<<"\n";
		coordinate.first=x;
		coordinate.second=y;
		return true;
	}else{
    return true;
	}
} 

//Optional menu option allowing for board sizes between 4X4 and 16X16
int AskUserForBoardSize(int client){
  //Get the board's size 
  while (1){
    cout << "Please enter the size of the reversi board" << std::endl; 
	send(client, "Please enter the size of the reversi board\n", 43, 0);
    const std::string input = GetInput(client); 
    int size = -1; 
    if ( IsInt(input,size) == false) 
    { 
      std::cout << "Please enter an integer value. " << std::endl; 
	  send(client, "Please enter an integer value. \n", 32, 0);
      continue; 
    } 
    if ( size < 4) 
    { 
      std::cout << "Please enter an integer value bigger than 4. " << std::endl; 
	  send(client, "Please enter an integer value bigger than 4. \n", 47, 0);
      continue; 
    } 
    if ( size > 16) 
    { 
      std::cout << "Please enter an integer value less than 16. " << std::endl; 
	  send(client, "Please enter an integer value less than 16. \n", 45, 0);
      continue; 
    } 
    return size; 
  } 
}

//inserts the possibleMove symbol into the proper locations of the board
void showPossibleMoves(int client){
	GameReversi tempValid(boardSize);
	tempValid.SetBoard(game.GetBoard());
	Vector<Pair> vals = tempValid.GetValidMoves(CurrentPlayer);
	for(int i=0; i<vals.size(); i++)
		tempValid.SetSquare(vals[i].first,vals[i].second,validMove);
	std::cout<<tempValid<<"\n";
	
	ostringstream osTempValid;
	osTempValid << tempValid;
	
	std::string sTempValid = osTempValid.str();
	send(client, sTempValid.c_str(), sTempValid.size(), 0);
	
}

//uses the current time as a randomizing seed to select from available moves
void moveRandomly(){
	srand ( time(NULL) );
	GameReversi tempValid(boardSize);
	tempValid.SetBoard(game.GetBoard());
	Vector< Pair > vals = tempValid.GetValidMoves(CurrentPlayer);
	int randomMove = rand() % vals.size();
	coordinate.first=vals[randomMove].first;coordinate.second=vals[randomMove].second;
}

//takes input from the user that will set definations for the game settings
int handlePregameInput(int client){
	while(1){
	
		String input = GetInput(client);

		if( strncmp(input.c_str(), "EXIT", 4) == 0) {
			return 0;
		}

		else if( strncmp(input.c_str(), "DISPLAY_ON", 10) == 0) {
			displayOn = true;
			cout<<"OK\n";
			send(client, "OK\n", 3, 0);
			break;
		}

		else if( strncmp(input.c_str(), "DISPLAY_OFF", 11) == 0) {
			displayOn = false;
			std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
			break;
		}
		
		else if( strncmp(input.c_str(), "BLACK", 5) == 0) {
			AIlevelP2=AIlevelP1;
		    AIlevelP1="OFF";
			std::cout<<"BLACK\n";
			send(client, "BLACK\n", 6, 0);
		}

		else if( strncmp(input.c_str(), "WHITE", 5) == 0) {
		    AIlevelP1=AIlevelP2;
		    AIlevelP2="OFF";
			std::cout<<"WHITE\n";
			send(client, "WHITE\n", 6, 0);
		}
		
		else if( strncmp(input.c_str(), "HARD_V_HARD", 11) == 0) {
			AIlevelP1="HARD";
			AIlevelP2="HARD";
			std::cout<<"HARD V HARD\n";
			send(client, "HARD V HARD\n", 12, 0);
		}

		else if( strncmp(input.c_str(), "HARD_V_MEDIUM", 13) == 0) {
			AIlevelP1="HARD";
			AIlevelP2="MEDIUM";
			std::cout<<"HARD V MEDIUM\n";
			send(client, "HARD V MEDIUM\n", 14, 0);
		}
		
		else if( strncmp(input.c_str(), "HARD_V_EASY", 11) == 0) {
			AIlevelP1="HARD";
			AIlevelP2="EASY";
			std::cout<<"HARD V EASY\n";
			send(client, "HARD V EASY\n", 12, 0);
		}
		
		else if( strncmp(input.c_str(), "MEDIUM_V_MEDIUM", 15) == 0) {
				AIlevelP1="MEDIUM";
				AIlevelP2="MEDIUM";
				std::cout<<"MEDIUM V MEDIUM\n";
				send(client, "MEDIUM V MEDIUM\n", 16, 0);			
		}
		
		else if( strncmp(input.c_str(), "MEDIUM_V_EASY", 13) == 0) {
			AIlevelP1="MEDIUM";
			AIlevelP2="EASY";
			std::cout<<"MEDIUM V EASY\n";
			send(client, "MEDIUM V EASY\n", 14, 0);
		}
		
		else if( strncmp(input.c_str(), "EASY_V_EASY", 11) == 0) {
			AIlevelP1="EASY";
			AIlevelP2="EASY";
			std::cout<<"EASY V EASY\n";
			send(client, "EASY v EASY\n", 12, 0);		
		}
			
		else if( strncmp(input.c_str(), "NO_AI", 5) == 0) {
		    AIlevelP1="OFF";
		    AIlevelP2="OFF";
		    std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
		}
		
		else if( strncmp(input.c_str(), "EASY", 4) == 0) {
		    Square aiPlayer = ((PlayerIsAI(player2))?(player2):(player1));
		    AIlevel(aiPlayer) ="EASY";
			std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
		}
		
		else if( strncmp(input.c_str(), "MEDIUM", 6) == 0) {
		    Square aiPlayer = ((PlayerIsAI(player2))?(player2):(player1));
		    AIlevel(aiPlayer) ="MEDIUM";
			std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
		}
		
		else if( strncmp(input.c_str(), "HARD", 4) == 0) {
		    Square aiPlayer = ((PlayerIsAI(player2))?(player2):(player1));
		    AIlevel(aiPlayer) ="HARD";
			std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
		}
		else if(input[0]=='P'){
		    if(input[1]=='1'){
		        AIlevelP1=input.substr(2);
                std::cout<<"OK\n";
				send(client, "OK\n", 3, 0);
		    }
		    else if(input[1]=='2'){
		        AIlevelP2=input.substr(2);
                std::cout<<"OK\n";
				send(client, "OK\n", 3, 0);
		    }
		    else{std::cout<<"ILLEGAL\n"; send(client, "ILLEGAL\n", 7, 0);}
		}
		//else if(input=="4X4"){
		else if( strncmp(input.c_str(), "4X4", 3) == 0) {
			std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
			boardSize=4;
		}

		//else if(input=="8X8"){
		else if( strncmp(input.c_str(), "8X8", 3) == 0) {
			std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
			boardSize=8;
		}
		//else if(input=="6"){
		else if( strncmp(input.c_str(), "6", 1) == 0) {
		    AIlevelP1="MEDIUM";
		    AIlevelP2="OFF";
		    std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
			std::cout<<"P1:HARD-AI, P2:Human\n";
			send(client, "P1:HARD-AI, P2:Human\n", 21, 0);
		    displayOn=true;
			break;
		}
		//else if(input=="7"){
		else if( strncmp(input.c_str(), "7", 1) == 0) {
		    AIlevelP2="MEDIUM";
		    AIlevelP1="OFF";
		    std::cout<<"OK\n";
			send(client, "OK\n", 3, 0);
			std::cout<<"P1:Human, P2:HARD-AI\n";
			send(client, "P1:Human, P2:HARD-AI\n", 21, 0);
		    displayOn=true;
			break;
		}
		//else if(input=="8"){
		else if( strncmp(input.c_str(), "8", 1) == 0) {
		    AIlevelP1="MEDIUM";
		    AIlevelP2="HARD";
		    std::cout<<"OK\n";
			std::cout<<"P1:"<<AIlevelP1<<"-AI, P2:"<<AIlevelP2<<"-AI\n";
		    displayOn=true;
			break;
		}
		//else if(input=="88"){
		else if( strncmp( input.c_str(), "88", 2) == 0) {
		    AIlevelP1="MEDIUM_DEBG";
		    AIlevelP2="HARD";
		    std::cout<<"OK\n";
			std::cout<<"P1:"<<AIlevelP1<<"-AI, P2:"<<AIlevelP2<<"-AI\n";
		    displayOn=true;
			break;
		}
		else{
			//if(input!="?"){std::cout<<"ILLEGAL\n";}
			if( strncmp(input.c_str(), "?", 1) != 0) {std::cout<<"ILLEGAL\n"; send(client, "ILLEGAL\n", 8, 0);}
			std::cout<<"WHITE, BLACK, EASY, MEDIUM, HARD, DISPLAY_ON, EXIT\n"; //TODO: Add more..
			send(client, "WHITE, BLACK, EASY, MEDIUM, HARD, DISPLAY_ON, EXIT\n", 51, 0);
		}		
	}
	return 1;
}

//forward decleration
Pair findBestMoveZ(Square forecastPlayer,int depth);

//forward decleration
Pair findBestMoveY(Square forecastPlayer,int depth);

//forward decleration
Pair findBestMoveX(Square forecastPlayer,int depth);

//receives all input during the execution of the game
int handleGameInput(int client){
	int MoveCount=0,numOfGamesCompleted=0,blackWins=0,whiteWins=0;
    while(numOfGamesCompleted<totalExecutions){
		
        if(displayOn){
            cout<<"-------------------\n";
			cout<<"Move:"<<MoveCount<<"\n";
            cout<<"Current Player:"<<CurrentPlayer<<"\n";
            cout<<((PlayerIsAI(CurrentPlayer))?("Waiting on AI"):("Human's Move"))<<"\n";
            cout<< game;
			
			
			ostringstream osCurrentPlayer;
			ostringstream osGame;
			osCurrentPlayer	<< CurrentPlayer;
			osGame << game;
			
			std::string sCurrentPlayer = "Current Player: " + osCurrentPlayer.str() + '\n';
			std::string sGame = osGame.str();
		
			send(client, "\n-------------------\n", 21, 0);
			send(client, sCurrentPlayer.c_str(), sCurrentPlayer.size(), 0);
			
			if(PlayerIsAI(CurrentPlayer)) {
				send(client, "Waiting on AI\n", 14, 0);
			} else {
				send(client, "Human's Move\n", 13, 0);
			}
			
			send(client, sGame.c_str(), sGame.size(), 0);
			
        }
        //Check if Current Player can actually make a move 
        if (game.GetValidMoves(CurrentPlayer).empty()==true){
			if(game.GetValidMoves(GetOtherPlayer(CurrentPlayer)).empty()==true){
				//no player has a move available - end game
				const int n1 = game.Count(player1); 
				const int n2 = game.Count(player2); //TODO: ADD IN TIE CASE
				std::string p1Name = (PlayerIsAI(player1))?(AIlevelP1+"-AI"):("Human");
				std::string p2Name = (PlayerIsAI(player2))?(AIlevelP2+"-AI"):("Human");
				stringstream ss;
				std::cout<< "The game has ended after "<<MoveCount<<" moves!\n"
						<<"Player1 ("<<p1Name<<")["<<player1<<RESET<<"] conquered "<<n1<<" squares.\n"
						<<"Player2 ("<<p2Name<<")["<<player2<<RESET<<"] conquered "<<n2<<" squares.\n";
				if(n1==n2){
					PrintOut("It's a Draw!\n",client);
					send(client, "The game has ended\n", 19, 0);
			
					ostringstream osP1Name;
					ostringstream osP2Name;
					
					ostringstream osPlayer1;
					ostringstream osPlayer2;
					
					ostringstream osN1;
					ostringstream osN2;
					
					osP1Name << p1Name;
					osP2Name << p2Name;
					
					osPlayer1 << player1;
					osPlayer2 << player2;
					
					osN1 << n1;
					osN2 << n2;
					
					std::string p1SquaresConquered = "Player1 (" + osP1Name.str() + ")[" + osPlayer1.str() + "] conquered " + osN1.str() + " squares.\n";
					std::string p2SquaresConquered = "Player2 (" + osP2Name.str() + ")[" + osPlayer2.str() + "] conquered " + osN2.str() + " squares.\n";
					
					send(client, p1SquaresConquered.c_str(), p1SquaresConquered.size(), 0);
					send(client, p2SquaresConquered.c_str(), p2SquaresConquered.size(), 0);
					
					send(client, "It was a draw!\n", 15, 0);
					
					
				}
				else{
					std::cout<<"The winner is Player"<<((n1>n2)?("1("+p1Name+")"):("2("+p2Name+")"))
						<<"\nCongratulations!\n\n";
					if(n1>n2)
						blackWins++;
					else
						whiteWins++;
				
					stringstream ss;
					ss << "The game has ended after "<<MoveCount<<" moves!\n";
					
					send(client, ss.str().c_str(), ss.str().size(), 0);
			
					ostringstream osP1Name;
					ostringstream osP2Name;
					
					ostringstream osPlayer1;
					ostringstream osPlayer2;
					
					ostringstream osN1;
					ostringstream osN2;
					
					osP1Name << p1Name;
					osP2Name << p2Name;
					
					osPlayer1 << player1;
					osPlayer2 << player2;
					
					osN1 << n1;
					osN2 << n2;
					
					std::string p1SquaresConquered = "Player1 (" + osP1Name.str() + ")[" + osPlayer1.str() + "] conquered " + osN1.str() + " squares.\n";
					std::string p2SquaresConquered = "Player2 (" + osP2Name.str() + ")[" + osPlayer2.str() + "] conquered " + osN2.str() + " squares.\n";
					
					send(client, p1SquaresConquered.c_str(), p1SquaresConquered.size(), 0);
					send(client, p2SquaresConquered.c_str(), p2SquaresConquered.size(), 0);
					
					std::string p1Wins = "The winner is Player1(";
					std::string p2Wins = "The winner is Player2(";
					
					if(n1 > n2) {
						p1Wins = p1Wins + p1Name + ")";
						send(client, p1Wins.c_str(), p1Wins.size(), 0);
					} else {
						p2Wins = p2Wins + p2Name + ")";
						send(client, p2Wins.c_str(), p2Wins.size(), 0);
					}
					send(client, "\nCongratulations!\n\n", 19, 0);
				}
				if(totalExecutions>1){
					std::stringstream winCountStringStream;
					winCountStringStream<<"Player1 ("<<p1Name<<") won "<<blackWins<<"\n"<<"Player2 ("<<p2Name<<")won "<<whiteWins<<"\n";
					PrintOut(winCountStringStream.str(),client);
				 numOfGamesCompleted++;
				 MoveCount=0;
				game.SetBoard(newBoard.GetBoard());
				 continue;
				}break;

			}else{
				//If Current Player cannot move, but other player can
				std::cout<<"Too bad! Player"<<int(CurrentPlayer)<<"("<<CurrentPlayer<<RESET<<") is unabled to do a valid move!\n"; 
				CurrentPlayer=GetOtherPlayer(CurrentPlayer);
				std::cout<<"The next turn again goes to Player"<<int(CurrentPlayer)<<"("<<CurrentPlayer<<RESET<<") !\n";
				
				ostringstream osMessage1;
				osMessage1 << "Too bad! Player"<<int(CurrentPlayer)<<"("<<CurrentPlayer<<RESET<<") is unabled to do a valid move!\n"; 
				
				send(client, osMessage1.str().c_str(), osMessage1.str().size(), 0);
				
				
				ostringstream osMessage2;			
				std::cout<<"The next turn again goes to Player"<<int(CurrentPlayer)<<"("<<CurrentPlayer<<RESET<<") !\n";
				osMessage2 << "The next turn again goes to Player"<<int(CurrentPlayer)<<"("<<CurrentPlayer<<RESET<<") !\n";
				send(client, osMessage2.str().c_str(), osMessage2.str().size(), 0);
				
				
				continue;
			}
        }
        MoveCount++;
        {//Input-Gathering Block
            if(PlayerIsAI(CurrentPlayer)){
                //CurrentPlayer is an AI
                if(AIlevel(CurrentPlayer)=="EASY"){
                    moveRandomly();
                }
				/*else if(AIlevel(CurrentPlayer)=="MEDIUM"){
					AlphaBetaAI ai(game, CurrentPlayer, 3);
					coordinate = ai.findMax();
				}*/
               else if(AIlevel(CurrentPlayer)=="MEDIUM"){
					std::pair<int,int> bestMove = findBestMoveY(CurrentPlayer,0);
					coordinate.first=bestMove.first;coordinate.second=bestMove.second;
               }

                else if(AIlevel(CurrentPlayer).substr(0,4)=="HARD"){
					std::pair<int,int> bestMove = findBestMoveX(CurrentPlayer,0);
					coordinate.first=bestMove.first;coordinate.second=bestMove.second;//38,26vsZ
                }
                std::cout<<AIlevel(CurrentPlayer)<<"-AI Plays:"<<char('A'+coordinate.first)<<coordinate.second+1<<"\n";
				
				
				ostringstream osAIlevel;
				ostringstream osCoordinates; 
				
				osAIlevel << AIlevel(CurrentPlayer);
				osCoordinates << char('A'+coordinate.first)<<coordinate.second+1;
				
				std::string message = osAIlevel.str() + "-AI Plays @:" + osCoordinates.str() + '\n';
				
				send(client, message.c_str(), message.size(), 0);
				
				
            }
            else if( !(PlayerIsAI(CurrentPlayer)) ) {
                //CurrentPlayer is a Human
                std::cout<<"::";
				send(client, "::", 2, 0);
                const std::string input = GetInput(client);
                const bool isValidCoordinate = IsCoordinate(input, coordinate); 
                if (isValidCoordinate == false){
                    //Input was not a Coordinate
                    //if(input=="EXIT") return 0;
					if( strncmp(input.c_str(), "EXIT", 4) == 0) return 0;
					// else if(input=="?"){
					else if( strncmp(input.c_str(), "?", 1) == 0) {
                        std::cout<<"Enter coordinates as # alpha values, DISPLAY_OFF, SHOW_NEXT_POS, UNDO, REDO, EXIT\n";
                        send(client, "Enter coordinates as # alpha values, DISPLAY_OFF, SHOW_NEXT_POS, UNDO, REDO, EXIT\n", 82, 0);
						continue;
                    }
                    //else if(input=="DISPLAY_OFF"){
					else if( strncmp(input.c_str(), "DISPLAY_OFF", 11) == 0) {
                        displayOn = false;
                        std::cout<<"OK\n";
						send(client, "OK\n", 3, 0);
                        continue;
                    }
                    //else if(input=="UNDO"){
					else if( strncmp(input.c_str(), "UNDO", 4) == 0) {
                        if(!game.DoUndo()){
                            std::cerr<<"CANNOT UNDO, NOT ENOUGH STATES IN STACK\n";
                            std::cout<<"ILLEGAL\n";
							send(client, "ILLEGAL\n", 8, 0);
                            continue;
                        }
                        continue;
                    }
                    //else if(input=="REDO"){
					else if( strncmp(input.c_str(), "REDO", 4) == 0) {
                        if(!game.DoRedo()){
                            std::cerr<<"CANNOT REDO, NOT ENOUGH STATES IN STACK\n";
                            std::cout<<"ILLEGAL\n";
							send(client, "ILLEGAL\n", 8, 0);
                            continue;
                        }
                        continue;
                    }
                    //else if(input=="SHOW_NEXT_POS"){
					else if( strncmp(input.c_str(), "SHOW_NEXT_POS", 13) == 0) {;
                        showPossibleMoves(client);
                        continue;
                    }
                    //else if(input=="RAND" || input=="R"){
					else if( strncmp(input.c_str(), "RAND", 4) == 0 || strncmp(input.c_str(), "R", 1) == 0 ) {
                        moveRandomly();
                    }
                    else{
                        std::cout << "ILLEGAL\n";
						send(client, "ILLEGAL\n", 8, 0);
                        continue;
                    }
                    
					/*
					if(input=="EXIT") return 0;
                    else if(input=="?"){
                        std::cout<<"Enter coordinates as # alpha values, DISPLAY_OFF, SHOW_NEXT_POS, UNDO, REDO, EXIT\n";
                        continue;
                    }
                    else if(input=="DISPLAY_OFF"){
                        displayOn = false;
                        std::cout<<"OK\n";
                        continue;
                    }
                    else if(input=="UNDO"){
                        if(!game.DoUndo()){
                            std::cerr<<"CANNOT UNDO, NOT ENOUGH STATES IN STACK\n";
                            std::cout<<"ILLEGAL\n";
                            continue;
                        }
                        continue;
                    }
                    else if(input=="REDO"){
                        if(!game.DoRedo()){
                            std::cerr<<"CANNOT REDO, NOT ENOUGH STATES IN STACK\n";
                            std::cout<<"ILLEGAL\n";
                            continue;
                        }
                        continue;
                    }
                    else if(input=="SHOW_NEXT_POS"){
                        showPossibleMoves();
                        continue;
                    }
                    else if(input=="RAND" || input=="R"){
                        moveRandomly();
                    }
                    else{
                        std::cout << "ILLEGAL\n";
                        continue;
                    }*/
                }
                if(isValidCoordinate){
                   //Valid coordinate was entered
                    if(displayOn){
                        std::cout<<"Human Plays :"<<char('A'+coordinate.first)<<coordinate.second+1<<"\n";
						
						ostringstream osCoordinates;
						osCoordinates<< "Human Plays @:"<< char('A'+coordinate.first)<<coordinate.second+1<<"\n";
						send(client, osCoordinates.str().c_str(), osCoordinates.str().size(), 0);
                    }
                }
            }//End Human Input
        }//End of Input-Gathering Block
        
        //Check that move is valid for Current Player.
        if (game.IsValidMove(coordinate.first, coordinate.second, CurrentPlayer) == false){
            std::cout << "ILLEGAL\n";
			send(client, "ILLEGAL\n", 8, 0);
            continue; 
        }

        //Actually do the move 
        game.DoMove(coordinate.first, coordinate.second, CurrentPlayer);

        //Switch Player's turns
        CurrentPlayer=GetOtherPlayer(CurrentPlayer); 
        if(displayOn){std::cout<<"-------------------\n\n"; send(client, "-------------------\n\n", 21, 0);}

    }
    return 1;
}

//Handles user input and display of data 
	int api(String commandLine,int client){
		if(client <= 0) {
			server = false;
		} else {
			server = true;
		}
		for(int i=0;i<40;i++)std::cout<<"\n";
		std::cout<< "WELCOME\n";
		send(client, "WELCOME\n", 8, 0);
		while(1){

			if(!handlePregameInput(client)){return 0;}
	
			std::cout<<"Player1: "<<((PlayerIsAI(player1))?("AI    :"):("Human :"))<<player1<<": BLACK\n"<<RESET;
			std::cout<<"Player2: "<<((PlayerIsAI(player2))?("AI    :"):("Human :"))<<player2<<": WHITE\n"<<RESET;
			
			ostringstream ssPlayer1;
			ostringstream ssPlayer2;
			std::string infoPlayer1;
			std::string infoPlayer2;
			
			if(PlayerIsAI(player1)) {
				ssPlayer1 << player1;
				infoPlayer1 = "Player1: AI\t:" + ssPlayer1.str() + ": BLACK\n";
			} else {
				ssPlayer1 << player1;
				infoPlayer1 = "Player1: Human\t:" + ssPlayer1.str() + ": BLACK\n";
			}
		
			if(PlayerIsAI(player2)) {
				ssPlayer2 << player2;
				infoPlayer2 = "Player2: AI\t:" + ssPlayer2.str() + ": WHITE\n";
			} else {
				ssPlayer2 << player2;
				infoPlayer2 = "Player2: Human\t:" + ssPlayer2.str() + ": WHITE\n";
			}
		
			send(client, infoPlayer1.c_str(), infoPlayer1.size(), 0);
			send(client, infoPlayer2.c_str(), infoPlayer2.size(), 0);
			
			if(!handleGameInput(client))
				return 0;
			std::cout << "Press ENTER to quit to continue.  Use the EXIT command a any time to quit.";
			send(client, "Press ENTER to quit to continue.  Use the EXIT command a any time to quit.\n", 75, 0);
						
			std::cin.ignore(numeric_limits<streamsize>::max(), '\n');
			game.SetBoard(newBoard.GetBoard());
			//return 0;
		}
		return 0;
	}
};
