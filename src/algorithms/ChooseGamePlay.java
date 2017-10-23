/**This class determines the type of game which will take place and 
 * determines the way moves are recorded and actuated:
Taken from text file (RECORDED) => board moves pieces automatically
User moves pieces (HUMAN) => moves tracked and app updated
Signal from app (REMOTE) => board moves pieces automatically
Calculated from AI (CPU) => board moves pieces automatically
 * 
 */

package algorithms;

import objects.Player;
import objects.PlayerType;

public class ChooseGamePlay {

	Player player1;
	Player player2;
	
	public ChooseGamePlay(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		setGame(player1, player2);
	}
	
	public void setGame(Player thisPlayer, Player opponent){
		if(thisPlayer.type == PlayerType.RECORDED && opponent.type == PlayerType.RECORDED) {
			//This is a loaded game (or live in-progress game of well-known professionals)
			//All moves are just loaded from data
		}else if(thisPlayer.type == PlayerType.CPU && opponent.type == PlayerType.CPU) {
			//setup a CPU vs CPU game (perhaps player wants to watch a running game
			//learn techniques or enjoy watching) each move will be calculated
		}else if(thisPlayer.type == PlayerType.CPU && opponent.type == PlayerType.HUMAN) {
			//If you want to play someone else, but 'you' are the computer, ie get to watch 
			//the moves the AI comes up with.
		}else if(thisPlayer.type == PlayerType.HUMAN && opponent.type == PlayerType.CPU) {
			//The original use case, a player wants a physical game but there is no opponent, 
			//so they play against the CPU which moves the pieces automatically
		}else if(thisPlayer.type == PlayerType.HUMAN && opponent.type == PlayerType.HUMAN) {
			//Both players are physically present (a standard game) so no actions need to be 
			//taken (but the board could move invalid moves back, or arrange the pieces etc.
		}else if(opponent.type == PlayerType.HUMAN && opponent.type == PlayerType.REMOTE) {
			//The next most appropriate use case - a player wants to play someone who is not 
			//physically present
		}else if(opponent.type == PlayerType.REMOTE && opponent.type == PlayerType.HUMAN) {
			//The player wants to control the board remotely and the opponent is present
		}else if(opponent.type == PlayerType.REMOTE && opponent.type == PlayerType.REMOTE){
			//A purely remote game, both players are moving pieces through the internet
			//to affect the board here.   (Could be showcase or challenge match)
		}else if(opponent.type == PlayerType.CPU && opponent.type == PlayerType.REMOTE){
			//The player is wanting to watch the computer play a remote friend eg to test them
		}else if(opponent.type == PlayerType.REMOTE && opponent.type == PlayerType.CPU) {
			//The player is not at their board but use the app to send their move
		}else{
			//No other valid gameplay types are available, notify user that they cannot play
			//this configuration and make them select again.
		}
		
	}
	
}
