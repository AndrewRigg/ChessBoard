package interfaces;

import org.sintef.jarduino.DigitalPin;
import org.sintef.jarduino.DigitalState;
import org.sintef.jarduino.JArduino;
import org.sintef.jarduino.PinMode;
import org.sintef.jarduino.comm.Serial4JArduino;
/**
 * This will be the place where the game engine can read in 
 * data from the sensors (eg board configuration from reed switches
 * and arduino, RFID tags from RFID reader) and can send output streams 
 * to the motors via controllers and can send signals to the leds and 7-segment
 * displays (through e.g. arduino).
 * @author Andrew
 * The JArduino libraries have been added to the build and the JArduino methods
 * will be used
 */
public class GameInputAndOutput extends JArduino{


public GameInputAndOutput(String port) {
    super(port);
}

@Override
protected void setup() {
    // initialize the digital pin as an output.
    // Pin 13 has an LED connected on most Arduino boards:
    pinMode(DigitalPin.PIN_12, PinMode.OUTPUT);
}

@Override
protected void loop() {
    // set the LED on
    digitalWrite(DigitalPin.PIN_12, DigitalState.HIGH);
    delay(1000); // wait for a second
    // set the LED off
    digitalWrite(DigitalPin.PIN_12, DigitalState.LOW);
    delay(1000); // wait for a second
}

public static void main(String[] args) {
    String serialPort;
    if (args.length == 1) {
        serialPort = args[0];
    } else {
        serialPort = Serial4JArduino.selectSerialPort();
    }
    JArduino arduino = new GameInputAndOutput(serialPort);
    arduino.runArduinoProcess();
}
}