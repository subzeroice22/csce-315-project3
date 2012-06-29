package csce315.game.reversi;

import java.util.Stack;
import java.util.Vector;

enum Square { empty, player1, player2, validMove };

public class GameReversi {
	
	//Member Variables
	public Vector<Vector<Square>> mBoard;
	public Stack<Vector<Vector<Square>>> boardHistory;
	public Stack<Vector<Vector<Square>>> boardRedos;
	public int size;
	
	public GameReversi(int size) {
		int x = size / 2 - 1;
		int y = size / 2 - 1;
		SetSquare(x, y, Square.player1);
		SetSquare(x+1, y, Square.player2);
		SetSquare(x, y+1, Square.player2);
		SetSquare(x+1, y+1, Square.player1);
	}
	
	//Member functions
	
	//done
	public Vector<Vector<Square>> GetBoard() {
		return mBoard;
	}
	
	//done
	public Square GetSquare(int x, int y) {
		return Global.GetSquareXy(mBoard,x,y);
	}
	
	//done
	public void DoMove(int x, int y, Square player) {
		boardHistory.push(mBoard);
		while(!boardRedos.empty()) {
			boardRedos.pop();			
		}
		
		if(IsValidMoveUp(x,y,player) == true) DoMoveUp(x,y-1,player);
		if(IsValidMoveUpLeft(x,y,player) == true) DoMoveUpLeft(x,y-1,player);
		if(IsValidMoveLeft(x,y,player) == true) DoMoveLeft(x-1,y,player);
		if(IsValidMoveDownLeft(x,y,player) == true) DoMoveDownLeft(x-1,y+1,player);
		if(IsValidMoveDown(x,y,player) == true) DoMoveDown(x,y+1,player);
		if(IsValidMoveDownRight(x,y,player) == true) DoMoveDownRight(x+1,y+1,player);
		if(IsValidMoveRight(x,y,player) == true) DoMoveRight(x+1,y,player);
		if(IsValidMoveUpRight(x,y,player) == true) DoMoveUpRight(x+1,y-1,player);
		SetSquare(x,y,player);		
	}
	
	//done
	public void setSize(int newSize) {
		size = newSize;
	}
	
	//done
	public boolean IsValidMove(int x, int y, Square player) {
		if(GetSquare(x,y)!=Square.empty)
			return false;
		
		if(IsValidMoveUp(x,y,player)==true) return true;
		if(IsValidMoveUpLeft(x,y,player)==true) return true;
		if(IsValidMoveLeft(x,y,player)==true) return true;
		if(IsValidMoveDownLeft(x,y,player)==true) return true;
		if(IsValidMoveDown(x,y,player)==true) return true;
		if(IsValidMoveDownRight(x,y,player)==true) return true;
		if(IsValidMoveRight(x,y,player)==true) return true;
		if(IsValidMoveUpRight(x,y,player)==true) return true;
		return false;
	}
	
	//done? this will probably cause of crash
	public Vector<Pair> GetValidMoves(Square player) {
		int size = mBoard.size();
		Vector<Pair> v = new Vector<Pair>();
		for(int y = 0; y != size; ++y) {
			for(int x = 0; x != size; ++x) {
				if(IsValidMove(x,y,player) == true) {
					Pair coord = new Pair(x,y);
					v.add(coord);
				}
			}
		}
		return v;
	}
	
	//done
	public int GetSize() {
		return size;
	}
	
	//done
	public int Count(Square player) {
		int size = GetSize();
		int sum = 0;
		for(int y = 0; y != size; ++y) {
			for(int x = 0; x != size; ++x) {
				if(mBoard.elementAt(x).elementAt(y) == player) ++sum;
			}
		}
		return sum;
	}
	
	//done
	public boolean DoUndo() {
		if(boardHistory.size()<2) return false;
		
		boardRedos.push(mBoard);
		boardRedos.push(boardHistory.peek());
		boardHistory.pop();	
		return true;
	}
	
	//done
	public boolean DoRedo() {
		if(boardRedos.size()<2) return false;
		
		boardHistory.push(mBoard);
		boardHistory.push(boardRedos.peek());
		boardRedos.pop();
		mBoard = boardRedos.peek();
		boardRedos.pop();
		return true;
	}
	
	//done
	public void SetBoard(Vector<Vector<Square>> newBoard) {
		mBoard = newBoard;
	}
	
	//done
	public boolean IsValidMoveUp(int x, int y, Square player) {
		int b = y - 1;
		if(b < 1) return false;
		if(GetSquare(x,b) != Global.GetOtherPlayer(player)) return false;
		while(b > 0) {
			b--;
			if(GetSquare(x,b) == player) return true;
			if(GetSquare(x,b) == Square.empty) return true;
		}
		return false;
	}
	
	//done
	public boolean IsValidMoveUpLeft(int x, int y, Square player) {
		int b = y - 1;
		if(b < 1) return false;
		if(GetSquare(x,b) != Global.GetOtherPlayer(player)) return false;
		while(b > 0) {
			b--;
			if(GetSquare(x,b) == player) return true;
			if(GetSquare(x,b) == Square.empty) return false;
		}
		return false;
	}
	
	//done
	public boolean IsValidMoveLeft(int x, int y, Square player) {
		int a = x - 1;
		int b = y - 1;
		if(a < 1 || b < 1) return false;
		if(GetSquare(a,b) != Global.GetOtherPlayer(player)) return false;
		while(a > 0 && b > 0) {
			a--;
			b--;
			if(GetSquare(a,b) == player) return true;
			if(GetSquare(a,b) == Square.empty) return false;
		}
		return false;
	}
	
	//done
	public boolean IsValidMoveDownLeft(int x, int y, Square player) {
		int size = mBoard.size();
		int a = x - 1;
		int b = y + 1;
		
		if(a < 1) return false;
		if(b > size - 1) return false;
		if(GetSquare(a,b) != Global.GetOtherPlayer(player)) return false;
		while(a > 0 && b < size - 1) {
			a--;
			b++;
			if(GetSquare(a,b) == player) return true;
			if(GetSquare(a,b) == Square.empty) return false;
		}
		return false;
	}

	//done
	public boolean IsValidMoveDown(int x, int y, Square player) {
		int size = mBoard.size();
		int b = y + 1;
		if(b > size - 1) return false;
		if(GetSquare(x,b) != Global.GetOtherPlayer(player)) return false;
		while(b < size - 1) {
			b++;
			if(GetSquare(x,b) == player) return true;
			if(GetSquare(x,b) == Square.empty) return false;
		}
		return false;
	}
	
	//done
	public boolean IsValidMoveDownRight(int x, int y, Square player) {
		int size = mBoard.size();
		int a = x + 1;
		int b = y + 1;
		if(a > size - 1) return false;
		if(b > size - 1) return false;
		if(GetSquare(a,b) != Global.GetOtherPlayer(player)) return false;
		while(a < size - 1 && b < size - 1) {
			b++;
			a++;
			if(GetSquare(a,b) == player) return true;
			if(GetSquare(a,b) == Square.empty) return false;
		}
		return false;
	}
	
	//done
	public boolean IsValidMoveRight(int x, int y, Square player) {
		int size = mBoard.size();
		int a = x + 1;
		if(a > size - 1) return false;
		if(GetSquare(a,y) != Global.GetOtherPlayer(player)) return false;
		while(a < size - 1) {
			a++;
			if(GetSquare(a,y) == player) return true;
			if(GetSquare(a,y) == Square.empty) return false;
		}
		return false;
	}
	
	//done
	public boolean IsValidMoveUpRight(int x, int y, Square player) {
		int size = mBoard.size();
		int a = x + 1;
		int b = y - 1;
		if(a > size - 1) return false;
		if(b < 1) return false;
		if(GetSquare(a,b) != Global.GetOtherPlayer(player)) return false;
		while(a < size - 1 && b > 0) {
			a++;
			b--;
			if(GetSquare(a,b) == player) return true;
			if(GetSquare(a,b) == Square.empty) return false;
		}
		return false;
	}
	
	//done
	public void DoMoveUp(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			--y;
		}
	}
	
	//done
	public void DoMoveUpLeft(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			--y;
			--x;
		}
	}
	
	//done
	public void DoMoveLeft(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			--x;
		}
	}
	
	//done
	public void DoMoveDownLeft(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			++y;
			--x;
		}
	}
	
	//done
	public void DoMoveDown(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			++y;
		}
	}
	
	//done
	public void DoMoveDownRight(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			++y;
			++x;
		}
	}
	
	//done
	public void DoMoveRight(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			++x;
		}
	}
	
	//done
	public void DoMoveUpRight(int x, int y, Square player) {
		while(GetSquare(x,y) == Global.GetOtherPlayer(player)) {
			SetSquare(x,y,player);
			--y;
			++x;
		}
	}
	
	
	//can't simply do: mBoard.elementAt(y).elementAt(x) = player
	//this may not work as well, I don't know if that's a deep
	//or shallow copy
	public void SetSquare(int x, int y, Square player) {
		player = mBoard.elementAt(y).elementAt(x);
	}
	
}



