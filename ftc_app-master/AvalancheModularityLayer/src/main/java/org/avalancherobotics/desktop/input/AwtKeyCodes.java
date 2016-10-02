package org.avalancherobotics.desktop.input;

import org.avalancherobotics.standalone.input.KeyCodes;
import java.awt.event.KeyEvent;

public class AwtKeyCodes extends KeyCodes
{
	public AwtKeyCodes()
	{
		init();
	}

	private void init()
	{
		// We must attempt to register AWT key codes first, so that we
		// have a measure of how many key code integers are taken.  This allows
		// us to use assignAndRegisterArbitraryKeyCode() to fill in gaps in the
		// Android key codes.
		// THIS MUST COME FIRST.
		assignAwtKeyCodes();
		registerAwtKeyCodes();

		// Now that we know how many possible key codes are provided by AWT,
		// we can begin adding novel ones using assignAndRegisterArbitraryKeyCode().
		assignAndroidKeyCodes();
		assignAndRegisterUnimplementedAndroidKeyCodes();
		registerAndroidKeyCodes();

		// These will be arbitrary keycodes, so they must be assigned after
		// all of the compile-time available (platform-specific) statically
		// assigned keycodes.
		assignArbitraryMouseKeyCodes();
		registerMouseKeyCodes();

		testAllKeyCodeFields();
	}

	private void assignAndRegisterUnimplementedAndroidKeyCodes()
	{
		ANDROID_11 = assignAndRegisterArbitraryKeyCode("ANDROID_11");
		ANDROID_12 = assignAndRegisterArbitraryKeyCode("ANDROID_12");
		ANDROID_3D_MODE = assignAndRegisterArbitraryKeyCode("ANDROID_3D_MODE");
		ANDROID_ALT_RIGHT = assignAndRegisterArbitraryKeyCode("ANDROID_ALT_RIGHT");
		ANDROID_APP_SWITCH = assignAndRegisterArbitraryKeyCode("ANDROID_APP_SWITCH");
		ANDROID_ASSIST = assignAndRegisterArbitraryKeyCode("ANDROID_ASSIST");
		ANDROID_AVR_INPUT = assignAndRegisterArbitraryKeyCode("ANDROID_AVR_INPUT");
		ANDROID_AVR_POWER = assignAndRegisterArbitraryKeyCode("ANDROID_AVR_POWER");
		ANDROID_BACK = assignAndRegisterArbitraryKeyCode("ANDROID_BACK");
		ANDROID_BOOKMARK = assignAndRegisterArbitraryKeyCode("ANDROID_BOOKMARK");
		ANDROID_BREAK = assignAndRegisterArbitraryKeyCode("ANDROID_BREAK");
		ANDROID_BRIGHTNESS_DOWN = assignAndRegisterArbitraryKeyCode("ANDROID_BRIGHTNESS_DOWN");
		ANDROID_BRIGHTNESS_UP = assignAndRegisterArbitraryKeyCode("ANDROID_BRIGHTNESS_UP");
		ANDROID_BUTTON_1 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_1");
		ANDROID_BUTTON_10 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_10");
		ANDROID_BUTTON_11 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_11");
		ANDROID_BUTTON_12 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_12");
		ANDROID_BUTTON_13 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_13");
		ANDROID_BUTTON_14 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_14");
		ANDROID_BUTTON_15 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_15");
		ANDROID_BUTTON_16 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_16");
		ANDROID_BUTTON_2 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_2");
		ANDROID_BUTTON_3 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_3");
		ANDROID_BUTTON_4 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_4");
		ANDROID_BUTTON_5 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_5");
		ANDROID_BUTTON_6 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_6");
		ANDROID_BUTTON_7 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_7");
		ANDROID_BUTTON_8 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_8");
		ANDROID_BUTTON_9 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_9");
		ANDROID_BUTTON_A = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_A");
		ANDROID_BUTTON_B = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_B");
		ANDROID_BUTTON_C = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_C");
		ANDROID_BUTTON_L1 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_L1");
		ANDROID_BUTTON_L2 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_L2");
		ANDROID_BUTTON_MODE = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_MODE");
		ANDROID_BUTTON_R1 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_R1");
		ANDROID_BUTTON_R2 = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_R2");
		ANDROID_BUTTON_SELECT = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_SELECT");
		ANDROID_BUTTON_START = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_START");
		ANDROID_BUTTON_THUMBL = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_THUMBL");
		ANDROID_BUTTON_THUMBR = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_THUMBR");
		ANDROID_BUTTON_X = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_X");
		ANDROID_BUTTON_Y = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_Y");
		ANDROID_BUTTON_Z = assignAndRegisterArbitraryKeyCode("ANDROID_BUTTON_Z");
		ANDROID_CALCULATOR = assignAndRegisterArbitraryKeyCode("ANDROID_CALCULATOR");
		ANDROID_CALENDAR = assignAndRegisterArbitraryKeyCode("ANDROID_CALENDAR");
		ANDROID_CALL = assignAndRegisterArbitraryKeyCode("ANDROID_CALL");
		ANDROID_CAMERA = assignAndRegisterArbitraryKeyCode("ANDROID_CAMERA");
		ANDROID_CAPTIONS = assignAndRegisterArbitraryKeyCode("ANDROID_CAPTIONS");
		ANDROID_CHANNEL_DOWN = assignAndRegisterArbitraryKeyCode("ANDROID_CHANNEL_DOWN");
		ANDROID_CHANNEL_UP = assignAndRegisterArbitraryKeyCode("ANDROID_CHANNEL_UP");
		ANDROID_CONTACTS = assignAndRegisterArbitraryKeyCode("ANDROID_CONTACTS");
		ANDROID_CTRL_RIGHT = assignAndRegisterArbitraryKeyCode("ANDROID_CTRL_RIGHT");
		ANDROID_DPAD_CENTER = assignAndRegisterArbitraryKeyCode("ANDROID_DPAD_CENTER");
		ANDROID_DVR = assignAndRegisterArbitraryKeyCode("ANDROID_DVR");
		ANDROID_EISU = assignAndRegisterArbitraryKeyCode("ANDROID_EISU");
		ANDROID_ENDCALL = assignAndRegisterArbitraryKeyCode("ANDROID_ENDCALL");
		ANDROID_ENVELOPE = assignAndRegisterArbitraryKeyCode("ANDROID_ENVELOPE");
		ANDROID_EXPLORER = assignAndRegisterArbitraryKeyCode("ANDROID_EXPLORER");
		ANDROID_FOCUS = assignAndRegisterArbitraryKeyCode("ANDROID_FOCUS");
		ANDROID_FORWARD = assignAndRegisterArbitraryKeyCode("ANDROID_FORWARD");
		ANDROID_FUNCTION = assignAndRegisterArbitraryKeyCode("ANDROID_FUNCTION");
		ANDROID_GUIDE = assignAndRegisterArbitraryKeyCode("ANDROID_GUIDE");
		ANDROID_HEADSETHOOK = assignAndRegisterArbitraryKeyCode("ANDROID_HEADSETHOOK");
		ANDROID_HENKAN = assignAndRegisterArbitraryKeyCode("ANDROID_HENKAN");
		ANDROID_HOME = assignAndRegisterArbitraryKeyCode("ANDROID_HOME");
		ANDROID_INFO = assignAndRegisterArbitraryKeyCode("ANDROID_INFO");
		ANDROID_KANA = assignAndRegisterArbitraryKeyCode("ANDROID_KANA");
		ANDROID_KATAKANA_HIRAGANA = assignAndRegisterArbitraryKeyCode("ANDROID_KATAKANA_HIRAGANA");
		ANDROID_LANGUAGE_SWITCH = assignAndRegisterArbitraryKeyCode("ANDROID_LANGUAGE_SWITCH");
		ANDROID_LAST_CHANNEL = assignAndRegisterArbitraryKeyCode("ANDROID_LAST_CHANNEL");
		ANDROID_MANNER_MODE = assignAndRegisterArbitraryKeyCode("ANDROID_MANNER_MODE");
		ANDROID_MEDIA_AUDIO_TRACK = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_AUDIO_TRACK");
		ANDROID_MEDIA_CLOSE = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_CLOSE");
		ANDROID_MEDIA_EJECT = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_EJECT");
		ANDROID_MEDIA_FAST_FORWARD = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_FAST_FORWARD");
		ANDROID_MEDIA_NEXT = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_NEXT");
		ANDROID_MEDIA_PLAY = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_PLAY");
		ANDROID_MEDIA_PLAY_PAUSE = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_PLAY_PAUSE");
		ANDROID_MEDIA_PREVIOUS = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_PREVIOUS");
		ANDROID_MEDIA_RECORD = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_RECORD");
		ANDROID_MEDIA_REWIND = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_REWIND");
		ANDROID_MEDIA_SKIP_BACKWARD = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_SKIP_BACKWARD");
		ANDROID_MEDIA_SKIP_FORWARD = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_SKIP_FORWARD");
		ANDROID_MEDIA_STEP_BACKWARD = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_STEP_BACKWARD");
		ANDROID_MEDIA_STEP_FORWARD = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_STEP_FORWARD");
		ANDROID_MEDIA_STOP = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_STOP");
		ANDROID_MEDIA_TOP_MENU = assignAndRegisterArbitraryKeyCode("ANDROID_MEDIA_TOP_MENU");
		ANDROID_MENU = assignAndRegisterArbitraryKeyCode("ANDROID_MENU");
		ANDROID_META_RIGHT = assignAndRegisterArbitraryKeyCode("ANDROID_META_RIGHT");
		ANDROID_MUHENKAN = assignAndRegisterArbitraryKeyCode("ANDROID_MUHENKAN");
		ANDROID_MUSIC = assignAndRegisterArbitraryKeyCode("ANDROID_MUSIC");
		ANDROID_MUTE = assignAndRegisterArbitraryKeyCode("ANDROID_MUTE");
		ANDROID_NAVIGATE_IN = assignAndRegisterArbitraryKeyCode("ANDROID_NAVIGATE_IN");
		ANDROID_NAVIGATE_NEXT = assignAndRegisterArbitraryKeyCode("ANDROID_NAVIGATE_NEXT");
		ANDROID_NAVIGATE_OUT = assignAndRegisterArbitraryKeyCode("ANDROID_NAVIGATE_OUT");
		ANDROID_NAVIGATE_PREVIOUS = assignAndRegisterArbitraryKeyCode("ANDROID_NAVIGATE_PREVIOUS");
		ANDROID_NOTIFICATION = assignAndRegisterArbitraryKeyCode("ANDROID_NOTIFICATION");
		ANDROID_NUM = assignAndRegisterArbitraryKeyCode("ANDROID_NUM");
		ANDROID_NUMPAD_ADD = assignAndRegisterArbitraryKeyCode("ANDROID_NUMPAD_ADD");
		ANDROID_NUMPAD_COMMA = assignAndRegisterArbitraryKeyCode("ANDROID_NUMPAD_COMMA");
		ANDROID_NUMPAD_ENTER = assignAndRegisterArbitraryKeyCode("ANDROID_NUMPAD_ENTER");
		ANDROID_NUMPAD_EQUALS = assignAndRegisterArbitraryKeyCode("ANDROID_NUMPAD_EQUALS");
		ANDROID_NUMPAD_MULTIPLY = assignAndRegisterArbitraryKeyCode("ANDROID_NUMPAD_MULTIPLY");
		ANDROID_NUMPAD_SUBTRACT = assignAndRegisterArbitraryKeyCode("ANDROID_NUMPAD_SUBTRACT");
		ANDROID_NUM_LOCK = assignAndRegisterArbitraryKeyCode("ANDROID_NUM_LOCK");
		ANDROID_PAIRING = assignAndRegisterArbitraryKeyCode("ANDROID_PAIRING");
		ANDROID_PICTSYMBOLS = assignAndRegisterArbitraryKeyCode("ANDROID_PICTSYMBOLS");
		ANDROID_POWER = assignAndRegisterArbitraryKeyCode("ANDROID_POWER");
		ANDROID_PROG_BLUE = assignAndRegisterArbitraryKeyCode("ANDROID_PROG_BLUE");
		ANDROID_PROG_GREEN = assignAndRegisterArbitraryKeyCode("ANDROID_PROG_GREEN");
		ANDROID_PROG_RED = assignAndRegisterArbitraryKeyCode("ANDROID_PROG_RED");
		ANDROID_PROG_YELLOW = assignAndRegisterArbitraryKeyCode("ANDROID_PROG_YELLOW");
		ANDROID_RO = assignAndRegisterArbitraryKeyCode("ANDROID_RO");
		ANDROID_SEARCH = assignAndRegisterArbitraryKeyCode("ANDROID_SEARCH");
		ANDROID_SETTINGS = assignAndRegisterArbitraryKeyCode("ANDROID_SETTINGS");
		ANDROID_SHIFT_RIGHT = assignAndRegisterArbitraryKeyCode("ANDROID_SHIFT_RIGHT");
		ANDROID_SLEEP = assignAndRegisterArbitraryKeyCode("ANDROID_SLEEP");
		ANDROID_SOFT_LEFT = assignAndRegisterArbitraryKeyCode("ANDROID_SOFT_LEFT");
		ANDROID_SOFT_RIGHT = assignAndRegisterArbitraryKeyCode("ANDROID_SOFT_RIGHT");
		ANDROID_STB_INPUT = assignAndRegisterArbitraryKeyCode("ANDROID_STB_INPUT");
		ANDROID_STB_POWER = assignAndRegisterArbitraryKeyCode("ANDROID_STB_POWER");
		ANDROID_SWITCH_CHARSET = assignAndRegisterArbitraryKeyCode("ANDROID_SWITCH_CHARSET");
		ANDROID_SYM = assignAndRegisterArbitraryKeyCode("ANDROID_SYM");
		ANDROID_SYSRQ = assignAndRegisterArbitraryKeyCode("ANDROID_SYSRQ");
		ANDROID_TV = assignAndRegisterArbitraryKeyCode("ANDROID_TV");
		ANDROID_TV_ANTENNA_CABLE = assignAndRegisterArbitraryKeyCode("ANDROID_TV_ANTENNA_CABLE");
		ANDROID_TV_AUDIO_DESCRIPTION = assignAndRegisterArbitraryKeyCode("ANDROID_TV_AUDIO_DESCRIPTION");
		ANDROID_TV_AUDIO_DESCRIPTION_MIX_DOWN = assignAndRegisterArbitraryKeyCode("ANDROID_TV_AUDIO_DESCRIPTION_MIX_DOWN");
		ANDROID_TV_AUDIO_DESCRIPTION_MIX_UP = assignAndRegisterArbitraryKeyCode("ANDROID_TV_AUDIO_DESCRIPTION_MIX_UP");
		ANDROID_TV_CONTENTS_MENU = assignAndRegisterArbitraryKeyCode("ANDROID_TV_CONTENTS_MENU");
		ANDROID_TV_DATA_SERVICE = assignAndRegisterArbitraryKeyCode("ANDROID_TV_DATA_SERVICE");
		ANDROID_TV_INPUT = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT");
		ANDROID_TV_INPUT_COMPONENT_1 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_COMPONENT_1");
		ANDROID_TV_INPUT_COMPONENT_2 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_COMPONENT_2");
		ANDROID_TV_INPUT_COMPOSITE_1 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_COMPOSITE_1");
		ANDROID_TV_INPUT_COMPOSITE_2 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_COMPOSITE_2");
		ANDROID_TV_INPUT_HDMI_1 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_HDMI_1");
		ANDROID_TV_INPUT_HDMI_2 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_HDMI_2");
		ANDROID_TV_INPUT_HDMI_3 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_HDMI_3");
		ANDROID_TV_INPUT_HDMI_4 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_HDMI_4");
		ANDROID_TV_INPUT_VGA_1 = assignAndRegisterArbitraryKeyCode("ANDROID_TV_INPUT_VGA_1");
		ANDROID_TV_NETWORK = assignAndRegisterArbitraryKeyCode("ANDROID_TV_NETWORK");
		ANDROID_TV_NUMBER_ENTRY = assignAndRegisterArbitraryKeyCode("ANDROID_TV_NUMBER_ENTRY");
		ANDROID_TV_POWER = assignAndRegisterArbitraryKeyCode("ANDROID_TV_POWER");
		ANDROID_TV_RADIO_SERVICE = assignAndRegisterArbitraryKeyCode("ANDROID_TV_RADIO_SERVICE");
		ANDROID_TV_SATELLITE = assignAndRegisterArbitraryKeyCode("ANDROID_TV_SATELLITE");
		ANDROID_TV_SATELLITE_BS = assignAndRegisterArbitraryKeyCode("ANDROID_TV_SATELLITE_BS");
		ANDROID_TV_SATELLITE_CS = assignAndRegisterArbitraryKeyCode("ANDROID_TV_SATELLITE_CS");
		ANDROID_TV_SATELLITE_SERVICE = assignAndRegisterArbitraryKeyCode("ANDROID_TV_SATELLITE_SERVICE");
		ANDROID_TV_TELETEXT = assignAndRegisterArbitraryKeyCode("ANDROID_TV_TELETEXT");
		ANDROID_TV_TERRESTRIAL_ANALOG = assignAndRegisterArbitraryKeyCode("ANDROID_TV_TERRESTRIAL_ANALOG");
		ANDROID_TV_TERRESTRIAL_DIGITAL = assignAndRegisterArbitraryKeyCode("ANDROID_TV_TERRESTRIAL_DIGITAL");
		ANDROID_TV_TIMER_PROGRAMMING = assignAndRegisterArbitraryKeyCode("ANDROID_TV_TIMER_PROGRAMMING");
		ANDROID_TV_ZOOM_MODE = assignAndRegisterArbitraryKeyCode("ANDROID_TV_ZOOM_MODE");
		ANDROID_VOICE_ASSIST = assignAndRegisterArbitraryKeyCode("ANDROID_VOICE_ASSIST");
		ANDROID_VOLUME_DOWN = assignAndRegisterArbitraryKeyCode("ANDROID_VOLUME_DOWN");
		ANDROID_VOLUME_MUTE = assignAndRegisterArbitraryKeyCode("ANDROID_VOLUME_MUTE");
		ANDROID_VOLUME_UP = assignAndRegisterArbitraryKeyCode("ANDROID_VOLUME_UP");
		ANDROID_WAKEUP = assignAndRegisterArbitraryKeyCode("ANDROID_WAKEUP");
		ANDROID_WINDOW = assignAndRegisterArbitraryKeyCode("ANDROID_WINDOW");
		ANDROID_YEN = assignAndRegisterArbitraryKeyCode("ANDROID_YEN");
		ANDROID_ZENKAKU_HANKAKU = assignAndRegisterArbitraryKeyCode("ANDROID_ZENKAKU_HANKAKU");
		ANDROID_ZOOM_IN = assignAndRegisterArbitraryKeyCode("ANDROID_ZOOM_IN");
		ANDROID_ZOOM_OUT = assignAndRegisterArbitraryKeyCode("ANDROID_ZOOM_OUT");
	}

	private void assignAwtKeyCodes()
	{
		/** AWT_0 thru AWT_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
		AWT_0 = java.awt.event.KeyEvent.VK_0;
		AWT_1 = java.awt.event.KeyEvent.VK_1;
		AWT_2 = java.awt.event.KeyEvent.VK_2;
		AWT_3 = java.awt.event.KeyEvent.VK_3;
		AWT_4 = java.awt.event.KeyEvent.VK_4;
		AWT_5 = java.awt.event.KeyEvent.VK_5;
		AWT_6 = java.awt.event.KeyEvent.VK_6;
		AWT_7 = java.awt.event.KeyEvent.VK_7;
		AWT_8 = java.awt.event.KeyEvent.VK_8;
		AWT_9 = java.awt.event.KeyEvent.VK_9;
		/** AWT_A thru AWT_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
		AWT_A = java.awt.event.KeyEvent.VK_A;
		/** Constant for the Accept or Commit function key. */
		AWT_ACCEPT = java.awt.event.KeyEvent.VK_ACCEPT;
		AWT_ADD = java.awt.event.KeyEvent.VK_ADD;
		AWT_AGAIN = java.awt.event.KeyEvent.VK_AGAIN;
		/** Constant for the All Candidates function key. */
		AWT_ALL_CANDIDATES = java.awt.event.KeyEvent.VK_ALL_CANDIDATES;
		/** Constant for the Alphanumeric function key. */
		AWT_ALPHANUMERIC = java.awt.event.KeyEvent.VK_ALPHANUMERIC;
		AWT_ALT = java.awt.event.KeyEvent.VK_ALT;
		/** Constant for the AltGraph function key. */
		AWT_ALT_GRAPH = java.awt.event.KeyEvent.VK_ALT_GRAPH;
		AWT_AMPERSAND = java.awt.event.KeyEvent.VK_AMPERSAND;
		AWT_ASTERISK = java.awt.event.KeyEvent.VK_ASTERISK;
		/** Constant for the "@" key. */
		AWT_AT = java.awt.event.KeyEvent.VK_AT;
		AWT_B = java.awt.event.KeyEvent.VK_B;
		AWT_BACK_QUOTE = java.awt.event.KeyEvent.VK_BACK_QUOTE;
		/** Constant for the back slash key, "\" */
		AWT_BACK_SLASH = java.awt.event.KeyEvent.VK_BACK_SLASH;
		AWT_BACK_SPACE = java.awt.event.KeyEvent.VK_BACK_SPACE;
		/** Constant for the Begin key. */
		AWT_BEGIN = java.awt.event.KeyEvent.VK_BEGIN;
		AWT_BRACELEFT = java.awt.event.KeyEvent.VK_BRACELEFT;
		AWT_BRACERIGHT = java.awt.event.KeyEvent.VK_BRACERIGHT;
		AWT_C = java.awt.event.KeyEvent.VK_C;
		AWT_CANCEL = java.awt.event.KeyEvent.VK_CANCEL;
		AWT_CAPS_LOCK = java.awt.event.KeyEvent.VK_CAPS_LOCK;
		/** Constant for the "^" key. */
		AWT_CIRCUMFLEX = java.awt.event.KeyEvent.VK_CIRCUMFLEX;
		AWT_CLEAR = java.awt.event.KeyEvent.VK_CLEAR;
		/** Constant for the close bracket key, "]" */
		AWT_CLOSE_BRACKET = java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
		/** Constant for the Code Input function key. */
		AWT_CODE_INPUT = java.awt.event.KeyEvent.VK_CODE_INPUT;
		/** Constant for the ":" key. */
		AWT_COLON = java.awt.event.KeyEvent.VK_COLON;
		/** Constant for the comma key, "," */
		AWT_COMMA = java.awt.event.KeyEvent.VK_COMMA;
		/** Constant for the Compose function key. */
		AWT_COMPOSE = java.awt.event.KeyEvent.VK_COMPOSE;
		/** Constant for the Microsoft Windows Context Menu key. */
		AWT_CONTEXT_MENU = java.awt.event.KeyEvent.VK_CONTEXT_MENU;
		AWT_CONTROL = java.awt.event.KeyEvent.VK_CONTROL;
		/** Constant for the Convert function key. */
		AWT_CONVERT = java.awt.event.KeyEvent.VK_CONVERT;
		AWT_COPY = java.awt.event.KeyEvent.VK_COPY;
		AWT_CUT = java.awt.event.KeyEvent.VK_CUT;
		AWT_D = java.awt.event.KeyEvent.VK_D;
		AWT_DEAD_ABOVEDOT = java.awt.event.KeyEvent.VK_DEAD_ABOVEDOT;
		AWT_DEAD_ABOVERING = java.awt.event.KeyEvent.VK_DEAD_ABOVERING;
		AWT_DEAD_ACUTE = java.awt.event.KeyEvent.VK_DEAD_ACUTE;
		AWT_DEAD_BREVE = java.awt.event.KeyEvent.VK_DEAD_BREVE;
		AWT_DEAD_CARON = java.awt.event.KeyEvent.VK_DEAD_CARON;
		AWT_DEAD_CEDILLA = java.awt.event.KeyEvent.VK_DEAD_CEDILLA;
		AWT_DEAD_CIRCUMFLEX = java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX;
		AWT_DEAD_DIAERESIS = java.awt.event.KeyEvent.VK_DEAD_DIAERESIS;
		AWT_DEAD_DOUBLEACUTE = java.awt.event.KeyEvent.VK_DEAD_DOUBLEACUTE;
		AWT_DEAD_GRAVE = java.awt.event.KeyEvent.VK_DEAD_GRAVE;
		AWT_DEAD_IOTA = java.awt.event.KeyEvent.VK_DEAD_IOTA;
		AWT_DEAD_MACRON = java.awt.event.KeyEvent.VK_DEAD_MACRON;
		AWT_DEAD_OGONEK = java.awt.event.KeyEvent.VK_DEAD_OGONEK;
		AWT_DEAD_SEMIVOICED_SOUND = java.awt.event.KeyEvent.VK_DEAD_SEMIVOICED_SOUND;
		AWT_DEAD_TILDE = java.awt.event.KeyEvent.VK_DEAD_TILDE;
		AWT_DEAD_VOICED_SOUND = java.awt.event.KeyEvent.VK_DEAD_VOICED_SOUND;
		AWT_DECIMAL = java.awt.event.KeyEvent.VK_DECIMAL;
		AWT_DELETE = java.awt.event.KeyEvent.VK_DELETE;
		AWT_DIVIDE = java.awt.event.KeyEvent.VK_DIVIDE;
		/** Constant for the "$" key. */
		AWT_DOLLAR = java.awt.event.KeyEvent.VK_DOLLAR;
		/** Constant for the non-numpad down arrow key. */
		AWT_DOWN = java.awt.event.KeyEvent.VK_DOWN;
		AWT_E = java.awt.event.KeyEvent.VK_E;
		AWT_END = java.awt.event.KeyEvent.VK_END;
		AWT_ENTER = java.awt.event.KeyEvent.VK_ENTER;
		/** Constant for the equals key, "=" */
		AWT_EQUALS = java.awt.event.KeyEvent.VK_EQUALS;
		AWT_ESCAPE = java.awt.event.KeyEvent.VK_ESCAPE;
		/** Constant for the Euro currency sign key. */
		AWT_EURO_SIGN = java.awt.event.KeyEvent.VK_EURO_SIGN;
		/** Constant for the "!" key. */
		AWT_EXCLAMATION_MARK = java.awt.event.KeyEvent.VK_EXCLAMATION_MARK;
		AWT_F = java.awt.event.KeyEvent.VK_F;
		AWT_F1 = java.awt.event.KeyEvent.VK_F1;
		AWT_F10 = java.awt.event.KeyEvent.VK_F10;
		AWT_F11 = java.awt.event.KeyEvent.VK_F11;
		AWT_F12 = java.awt.event.KeyEvent.VK_F12;
		AWT_F13 = java.awt.event.KeyEvent.VK_F13;
		AWT_F14 = java.awt.event.KeyEvent.VK_F14;
		AWT_F15 = java.awt.event.KeyEvent.VK_F15;
		AWT_F16 = java.awt.event.KeyEvent.VK_F16;
		AWT_F17 = java.awt.event.KeyEvent.VK_F17;
		AWT_F18 = java.awt.event.KeyEvent.VK_F18;
		AWT_F19 = java.awt.event.KeyEvent.VK_F19;
		AWT_F2 = java.awt.event.KeyEvent.VK_F2;
		AWT_F20 = java.awt.event.KeyEvent.VK_F20;
		AWT_F21 = java.awt.event.KeyEvent.VK_F21;
		AWT_F22 = java.awt.event.KeyEvent.VK_F22;
		AWT_F23 = java.awt.event.KeyEvent.VK_F23;
		AWT_F24 = java.awt.event.KeyEvent.VK_F24;
		AWT_F3 = java.awt.event.KeyEvent.VK_F3;
		AWT_F4 = java.awt.event.KeyEvent.VK_F4;
		AWT_F5 = java.awt.event.KeyEvent.VK_F5;
		AWT_F6 = java.awt.event.KeyEvent.VK_F6;
		AWT_F7 = java.awt.event.KeyEvent.VK_F7;
		AWT_F8 = java.awt.event.KeyEvent.VK_F8;
		AWT_F9 = java.awt.event.KeyEvent.VK_F9;
		AWT_FINAL = java.awt.event.KeyEvent.VK_FINAL;
		AWT_FIND = java.awt.event.KeyEvent.VK_FIND;
		AWT_FULL_WIDTH = java.awt.event.KeyEvent.VK_FULL_WIDTH;
		/** Constant for the Full-Width Characters function key. */
		AWT_G = java.awt.event.KeyEvent.VK_G;
		AWT_GREATER = java.awt.event.KeyEvent.VK_GREATER;
		AWT_H = java.awt.event.KeyEvent.VK_H;
		/** Constant for the Half-Width Characters function key. */
		AWT_HALF_WIDTH = java.awt.event.KeyEvent.VK_HALF_WIDTH;
		AWT_HELP = java.awt.event.KeyEvent.VK_HELP;
		/** Constant for the Hiragana function key. */
		AWT_HIRAGANA = java.awt.event.KeyEvent.VK_HIRAGANA;
		AWT_HOME = java.awt.event.KeyEvent.VK_HOME;
		AWT_I = java.awt.event.KeyEvent.VK_I;
		/** Constant for the input method on/off key. */
		AWT_INPUT_METHOD_ON_OFF = java.awt.event.KeyEvent.VK_INPUT_METHOD_ON_OFF;
		AWT_INSERT = java.awt.event.KeyEvent.VK_INSERT;
		/** Constant for the inverted exclamation mark key. */
		AWT_INVERTED_EXCLAMATION_MARK = java.awt.event.KeyEvent.VK_INVERTED_EXCLAMATION_MARK;
		AWT_J = java.awt.event.KeyEvent.VK_J;
		/** Constant for the Japanese-Hiragana function key. */
		AWT_JAPANESE_HIRAGANA = java.awt.event.KeyEvent.VK_JAPANESE_HIRAGANA;
		/** Constant for the Japanese-Katakana function key. */
		AWT_JAPANESE_KATAKANA = java.awt.event.KeyEvent.VK_JAPANESE_KATAKANA;
		/** Constant for the Japanese-Roman function key. */
		AWT_JAPANESE_ROMAN = java.awt.event.KeyEvent.VK_JAPANESE_ROMAN;
		AWT_K = java.awt.event.KeyEvent.VK_K;
		AWT_KANA = java.awt.event.KeyEvent.VK_KANA;
		/** Constant for the locking Kana function key. */
		AWT_KANA_LOCK = java.awt.event.KeyEvent.VK_KANA_LOCK;
		AWT_KANJI = java.awt.event.KeyEvent.VK_KANJI;
		/** Constant for the Katakana function key. */
		AWT_KATAKANA = java.awt.event.KeyEvent.VK_KATAKANA;
		/** Constant for the numeric keypad down arrow key. */
		AWT_KP_DOWN = java.awt.event.KeyEvent.VK_KP_DOWN;
		/** Constant for the numeric keypad left arrow key. */
		AWT_KP_LEFT = java.awt.event.KeyEvent.VK_KP_LEFT;
		/** Constant for the numeric keypad right arrow key. */
		AWT_KP_RIGHT = java.awt.event.KeyEvent.VK_KP_RIGHT;
		/** Constant for the numeric keypad up arrow key. */
		AWT_KP_UP = java.awt.event.KeyEvent.VK_KP_UP;
		AWT_L = java.awt.event.KeyEvent.VK_L;
		/** Constant for the non-numpad left arrow key. */
		AWT_LEFT = java.awt.event.KeyEvent.VK_LEFT;
		/** Constant for the "(" key. */
		AWT_LEFT_PARENTHESIS = java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS;
		AWT_LESS = java.awt.event.KeyEvent.VK_LESS;
		AWT_M = java.awt.event.KeyEvent.VK_M;
		AWT_META = java.awt.event.KeyEvent.VK_META;
		/** Constant for the minus key, "-" */
		AWT_MINUS = java.awt.event.KeyEvent.VK_MINUS;
		AWT_MODECHANGE = java.awt.event.KeyEvent.VK_MODECHANGE;
		AWT_MULTIPLY = java.awt.event.KeyEvent.VK_MULTIPLY;
		AWT_N = java.awt.event.KeyEvent.VK_N;
		/** Constant for the Don't Convert function key. */
		AWT_NONCONVERT = java.awt.event.KeyEvent.VK_NONCONVERT;
		AWT_NUM_LOCK = java.awt.event.KeyEvent.VK_NUM_LOCK;
		/** Constant for the "#" key. */
		AWT_NUMBER_SIGN = java.awt.event.KeyEvent.VK_NUMBER_SIGN;
		AWT_NUMPAD0 = java.awt.event.KeyEvent.VK_NUMPAD0;
		AWT_NUMPAD1 = java.awt.event.KeyEvent.VK_NUMPAD1;
		AWT_NUMPAD2 = java.awt.event.KeyEvent.VK_NUMPAD2;
		AWT_NUMPAD3 = java.awt.event.KeyEvent.VK_NUMPAD3;
		AWT_NUMPAD4 = java.awt.event.KeyEvent.VK_NUMPAD4;
		AWT_NUMPAD5 = java.awt.event.KeyEvent.VK_NUMPAD5;
		AWT_NUMPAD6 = java.awt.event.KeyEvent.VK_NUMPAD6;
		AWT_NUMPAD7 = java.awt.event.KeyEvent.VK_NUMPAD7;
		AWT_NUMPAD8 = java.awt.event.KeyEvent.VK_NUMPAD8;
		AWT_NUMPAD9 = java.awt.event.KeyEvent.VK_NUMPAD9;
		AWT_O = java.awt.event.KeyEvent.VK_O;
		/** Constant for the open bracket key, "[" */
		AWT_OPEN_BRACKET = java.awt.event.KeyEvent.VK_OPEN_BRACKET;
		AWT_P = java.awt.event.KeyEvent.VK_P;
		AWT_PAGE_DOWN = java.awt.event.KeyEvent.VK_PAGE_DOWN;
		AWT_PAGE_UP = java.awt.event.KeyEvent.VK_PAGE_UP;
		AWT_PASTE = java.awt.event.KeyEvent.VK_PASTE;
		AWT_PAUSE = java.awt.event.KeyEvent.VK_PAUSE;
		/** Constant for the period key, "." */
		AWT_PERIOD = java.awt.event.KeyEvent.VK_PERIOD;
		/** Constant for the "+" key. */
		AWT_PLUS = java.awt.event.KeyEvent.VK_PLUS;
		/** Constant for the Previous Candidate function key. */
		AWT_PREVIOUS_CANDIDATE = java.awt.event.KeyEvent.VK_PREVIOUS_CANDIDATE;
		AWT_PRINTSCREEN = java.awt.event.KeyEvent.VK_PRINTSCREEN;
		AWT_PROPS = java.awt.event.KeyEvent.VK_PROPS;
		AWT_Q = java.awt.event.KeyEvent.VK_Q;
		AWT_QUOTE = java.awt.event.KeyEvent.VK_QUOTE;
		AWT_QUOTEDBL = java.awt.event.KeyEvent.VK_QUOTEDBL;
		AWT_R = java.awt.event.KeyEvent.VK_R;
		/** Constant for the non-numpad right arrow key. */
		AWT_RIGHT = java.awt.event.KeyEvent.VK_RIGHT;
		/** Constant for the ")" key. */
		AWT_RIGHT_PARENTHESIS = java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS;
		/** Constant for the Roman Characters function key. */
		AWT_ROMAN_CHARACTERS = java.awt.event.KeyEvent.VK_ROMAN_CHARACTERS;
		AWT_S = java.awt.event.KeyEvent.VK_S;
		AWT_SCROLL_LOCK = java.awt.event.KeyEvent.VK_SCROLL_LOCK;
		/** Constant for the semicolon key, ";" */
		AWT_SEMICOLON = java.awt.event.KeyEvent.VK_SEMICOLON;
		/** Constant for the Numpad Separator key. */
		AWT_SEPARATOR = java.awt.event.KeyEvent.VK_SEPARATOR;
		AWT_SHIFT = java.awt.event.KeyEvent.VK_SHIFT;
		/** Constant for the forward slash key, "/" */
		AWT_SLASH = java.awt.event.KeyEvent.VK_SLASH;
		AWT_SPACE = java.awt.event.KeyEvent.VK_SPACE;
		AWT_STOP = java.awt.event.KeyEvent.VK_STOP;
		AWT_SUBTRACT = java.awt.event.KeyEvent.VK_SUBTRACT;
		AWT_T = java.awt.event.KeyEvent.VK_T;
		AWT_TAB = java.awt.event.KeyEvent.VK_TAB;
		AWT_U = java.awt.event.KeyEvent.VK_U;
		/** This value is used to indicate that the keyCode is unknown. */
		AWT_UNDEFINED = java.awt.event.KeyEvent.VK_UNDEFINED;
		/** Constant for the "_" key. */
		AWT_UNDERSCORE = java.awt.event.KeyEvent.VK_UNDERSCORE;
		AWT_UNDO = java.awt.event.KeyEvent.VK_UNDO;
		/** Constant for the non-numpad up arrow key. */
		AWT_UP = java.awt.event.KeyEvent.VK_UP;
		AWT_V = java.awt.event.KeyEvent.VK_V;
		AWT_W = java.awt.event.KeyEvent.VK_W;

		/** Constant for the Microsoft Windows "Windows" key. */
		AWT_WINDOWS = java.awt.event.KeyEvent.VK_WINDOWS;
		AWT_X = java.awt.event.KeyEvent.VK_X;
		AWT_Y = java.awt.event.KeyEvent.VK_Y;
		AWT_Z = java.awt.event.KeyEvent.VK_Z;
	}

	private void assignAndroidKeyCodes()
	{
		ANDROID_0 = java.awt.event.KeyEvent.VK_0;
		ANDROID_1 = java.awt.event.KeyEvent.VK_1;
		ANDROID_2 = java.awt.event.KeyEvent.VK_2;
		ANDROID_3 = java.awt.event.KeyEvent.VK_3;
		ANDROID_4 = java.awt.event.KeyEvent.VK_4;
		ANDROID_5 = java.awt.event.KeyEvent.VK_5;
		ANDROID_6 = java.awt.event.KeyEvent.VK_6;
		ANDROID_7 = java.awt.event.KeyEvent.VK_7;
		ANDROID_8 = java.awt.event.KeyEvent.VK_8;
		ANDROID_9 = java.awt.event.KeyEvent.VK_9;
		ANDROID_A = java.awt.event.KeyEvent.VK_A;
		ANDROID_ALT_LEFT = java.awt.event.KeyEvent.VK_ALT;
		ANDROID_APOSTROPHE = java.awt.event.KeyEvent.VK_QUOTE;
		ANDROID_AT = java.awt.event.KeyEvent.VK_AT;
		ANDROID_B = java.awt.event.KeyEvent.VK_B;
		ANDROID_BACKSLASH = java.awt.event.KeyEvent.VK_BACK_SLASH;
		ANDROID_C = java.awt.event.KeyEvent.VK_C;
		ANDROID_CAPS_LOCK = java.awt.event.KeyEvent.VK_CAPS_LOCK;
		ANDROID_CLEAR = java.awt.event.KeyEvent.VK_CLEAR;
		ANDROID_COMMA = java.awt.event.KeyEvent.VK_COMMA;
		ANDROID_CTRL_LEFT = java.awt.event.KeyEvent.VK_CONTROL;
		ANDROID_D = java.awt.event.KeyEvent.VK_D;
		ANDROID_DEL = java.awt.event.KeyEvent.VK_BACK_SPACE;
		ANDROID_DPAD_DOWN = java.awt.event.KeyEvent.VK_DOWN;
		ANDROID_DPAD_LEFT = java.awt.event.KeyEvent.VK_LEFT;
		ANDROID_DPAD_RIGHT = java.awt.event.KeyEvent.VK_RIGHT;
		ANDROID_DPAD_UP = java.awt.event.KeyEvent.VK_UP;
		ANDROID_E = java.awt.event.KeyEvent.VK_E;
		ANDROID_ENTER = java.awt.event.KeyEvent.VK_ENTER;
		ANDROID_EQUALS = java.awt.event.KeyEvent.VK_EQUALS;
		ANDROID_ESCAPE = java.awt.event.KeyEvent.VK_ESCAPE;
		ANDROID_F = java.awt.event.KeyEvent.VK_F;
		ANDROID_F1 = java.awt.event.KeyEvent.VK_F1;
		ANDROID_F10 = java.awt.event.KeyEvent.VK_F10;
		ANDROID_F11 = java.awt.event.KeyEvent.VK_F11;
		ANDROID_F12 = java.awt.event.KeyEvent.VK_F12;
		ANDROID_F2 = java.awt.event.KeyEvent.VK_F2;
		ANDROID_F3 = java.awt.event.KeyEvent.VK_F3;
		ANDROID_F4 = java.awt.event.KeyEvent.VK_F4;
		ANDROID_F5 = java.awt.event.KeyEvent.VK_F5;
		ANDROID_F6 = java.awt.event.KeyEvent.VK_F6;
		ANDROID_F7 = java.awt.event.KeyEvent.VK_F7;
		ANDROID_F8 = java.awt.event.KeyEvent.VK_F8;
		ANDROID_F9 = java.awt.event.KeyEvent.VK_F9;
		ANDROID_FORWARD_DEL = java.awt.event.KeyEvent.VK_DELETE;
		ANDROID_G = java.awt.event.KeyEvent.VK_G;
		ANDROID_GRAVE = java.awt.event.KeyEvent.VK_BACK_QUOTE;
		ANDROID_H = java.awt.event.KeyEvent.VK_H;
		ANDROID_HELP = java.awt.event.KeyEvent.VK_HELP;
		ANDROID_I = java.awt.event.KeyEvent.VK_I;
		ANDROID_INSERT = java.awt.event.KeyEvent.VK_INSERT;
		ANDROID_J = java.awt.event.KeyEvent.VK_J;
		ANDROID_K = java.awt.event.KeyEvent.VK_K;
		ANDROID_L = java.awt.event.KeyEvent.VK_L;
		ANDROID_LEFT_BRACKET = java.awt.event.KeyEvent.VK_OPEN_BRACKET;
		ANDROID_M = java.awt.event.KeyEvent.VK_M;
		ANDROID_MEDIA_PAUSE = java.awt.event.KeyEvent.VK_PAUSE;
		ANDROID_META_LEFT = java.awt.event.KeyEvent.VK_META;
		ANDROID_MINUS = java.awt.event.KeyEvent.VK_MINUS;
		ANDROID_MOVE_END = java.awt.event.KeyEvent.VK_END;
		ANDROID_MOVE_HOME = java.awt.event.KeyEvent.VK_HOME;
		ANDROID_N = java.awt.event.KeyEvent.VK_N;
		ANDROID_NUMPAD_0 = java.awt.event.KeyEvent.VK_NUMPAD0;
		ANDROID_NUMPAD_1 = java.awt.event.KeyEvent.VK_NUMPAD1;
		ANDROID_NUMPAD_2 = java.awt.event.KeyEvent.VK_NUMPAD2;
		ANDROID_NUMPAD_3 = java.awt.event.KeyEvent.VK_NUMPAD3;
		ANDROID_NUMPAD_4 = java.awt.event.KeyEvent.VK_NUMPAD4;
		ANDROID_NUMPAD_5 = java.awt.event.KeyEvent.VK_NUMPAD5;
		ANDROID_NUMPAD_6 = java.awt.event.KeyEvent.VK_NUMPAD6;
		ANDROID_NUMPAD_7 = java.awt.event.KeyEvent.VK_NUMPAD7;
		ANDROID_NUMPAD_8 = java.awt.event.KeyEvent.VK_NUMPAD8;
		ANDROID_NUMPAD_9 = java.awt.event.KeyEvent.VK_NUMPAD9;
		ANDROID_NUMPAD_DIVIDE = java.awt.event.KeyEvent.VK_DIVIDE;
		ANDROID_NUMPAD_DOT = java.awt.event.KeyEvent.VK_DECIMAL;
		ANDROID_NUMPAD_LEFT_PAREN = java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS;
		ANDROID_NUMPAD_RIGHT_PAREN = java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS;
		ANDROID_O = java.awt.event.KeyEvent.VK_O;
		ANDROID_P = java.awt.event.KeyEvent.VK_P;
		ANDROID_PAGE_DOWN = java.awt.event.KeyEvent.VK_PAGE_DOWN;
		ANDROID_PAGE_UP = java.awt.event.KeyEvent.VK_PAGE_UP;
		ANDROID_PERIOD = java.awt.event.KeyEvent.VK_PERIOD;
		ANDROID_PLUS = java.awt.event.KeyEvent.VK_PLUS;
		ANDROID_POUND = java.awt.event.KeyEvent.VK_NUMBER_SIGN;
		ANDROID_Q = java.awt.event.KeyEvent.VK_Q;
		ANDROID_R = java.awt.event.KeyEvent.VK_R;
		ANDROID_RIGHT_BRACKET = java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
		ANDROID_S = java.awt.event.KeyEvent.VK_S;
		ANDROID_SCROLL_LOCK = java.awt.event.KeyEvent.VK_SCROLL_LOCK;
		ANDROID_SEMICOLON = java.awt.event.KeyEvent.VK_SEMICOLON;
		ANDROID_SHIFT_LEFT = java.awt.event.KeyEvent.VK_SHIFT;
		ANDROID_SLASH = java.awt.event.KeyEvent.VK_SLASH;
		ANDROID_SPACE = java.awt.event.KeyEvent.VK_SPACE;
		ANDROID_STAR = java.awt.event.KeyEvent.VK_MULTIPLY;
		ANDROID_T = java.awt.event.KeyEvent.VK_T;
		ANDROID_TAB = java.awt.event.KeyEvent.VK_TAB;
		ANDROID_TV_MEDIA_CONTEXT_MENU = java.awt.event.KeyEvent.VK_CONTEXT_MENU;
		ANDROID_U = java.awt.event.KeyEvent.VK_U;
		ANDROID_UNKNOWN = java.awt.event.KeyEvent.VK_UNDEFINED;
		ANDROID_V = java.awt.event.KeyEvent.VK_V;
		ANDROID_W = java.awt.event.KeyEvent.VK_W;
		ANDROID_X = java.awt.event.KeyEvent.VK_X;
		ANDROID_Y = java.awt.event.KeyEvent.VK_Y;
		ANDROID_Z = java.awt.event.KeyEvent.VK_Z;
	}
}