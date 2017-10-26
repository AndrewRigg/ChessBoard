package speech;

import java.util.ArrayList;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import objects.Piece;

/**
 * This class will deal with the API used to generate speech
 * from text (FreeTTS or ideally upgrade to Google Translate).
 * @author Andrew
 *
 */
public class CommandTextToSpeech {

	
	private static final String VOICE = "kevin";
	ArrayList<Integer> rows;
	ArrayList<Character> cols;
	
	
	public CommandTextToSpeech() {
		rows = new ArrayList<>();
		cols = new ArrayList<>();
		for(int i = 1, a = 'a'; i <= 8; i ++, a++){
			rows.add(i);
			cols.add((char) a);
		}
	}
	
	
	
	
	/**
	 * Set the string to be read out by the text-to-speech program
	 * (this allows the CPU to let the player know what has been moved)
	 * @param piece
	 * @param colFrom
	 * @param rowFrom
	 * @param colTo
	 * @param rowTo
	 * @return
	 */
	public String getStringCommand(Piece piece,  int colFrom, int rowFrom,  int colTo, int rowTo, boolean capture, boolean castling){
		String command = "";
		if(castling) {
			if(colTo < 5) {
				command = "Queenside Castle";
			}else {
				command = "Kingside Castle";
			}
		}else {
			command = piece.getType() + " from " + cols.get(colFrom-1) + rows.get(9-rowFrom) + " to " +  cols.get(colTo-1) + rows.get(9-rowTo);
		}
		if (capture) {
			command += "Capture";
		}
		return command;
	}
	
	/**
	 * FreeTTS text-to-speech facility for reading out moves for CPU (and player).
	 * 
	 */
	 public void speak(String text) {
		  Voice voice;
		  VoiceManager voiceManager = VoiceManager.getInstance();
		  voice = voiceManager.getVoice(VOICE);
		  voice.allocate();
		  voice.speak(text);
		 }
	
	
}
