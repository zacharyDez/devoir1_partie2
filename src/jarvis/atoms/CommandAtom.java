package jarvis.atoms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import jarvis.exceptions.UndefinedSymbolException;
import jarvis.interpreter.JarvisInterpreter;

/*
 * Attention de ne pas vous �tourdir avec cette classe.
 * Pas mal bord�lique, sert � aiguiller le comportement
 * de l'interpr�teur selon ce qu'il a lu.
 * La plupart des cas dont simples, certains sont complexes
 * et importants.
 * Pour ajouter des options et des capacit�s propres au d�buggage,
 * c'est par ici qu'il faut regarder.
 *  
 */

public class CommandAtom extends AbstractAtom {

	public static final String CMD_QUIT = "!q";
	public static final String CMD_ARG = "!a";
	public static final String CMD_UI = "!ui";
	public static final String CMD_ENV = "!e";
	public static final String CMD_ENVALL = "!ee";
	public static final String CMD_MESSAGE = "!(";
	public static final String CMD_ENDOFENVIRONMENT = "!eoc";
	public static final String CMD_ENDOFCLOSURE = "}";
	public static final String CMD_CLEAR = "!c";
	public static final String CMD_DEBUG = "!debug";
	public static final String CMD_ARGS = "!args";
	public static final String CMD_REF = "!ref";
	public static final String CMD_CLOSURE = "!{";
	public static final String CMD_LIST = "(";
	public static final String CMD_ENDOFLIST = ")";
	public static final String CMD_DICT = "[";
	public static final String CMD_ENDOFDICT = "]";
	public static final String CMD_ERROR = "!error";
	public static final String CMD_PEEK = "!p";
	public static final String CMD_MUTE = "!mute";
	public static final String CMD_UNMUTE = "!unmute";
	public static final String CMD_LOAD = "!load";
	public static final String CMD_NULL = "null";
	public static final String CMD_TRUE = "true";
	public static final String CMD_FALSE = "false";
	public static final String CMD_LOADMENU = "!ld";

	private static boolean dontPut = false;
	private static boolean dontInterpret = false;
	private static boolean acceptUndefined = false;

	private String value;

	/*
	 * Trois situations sp�ciales arrivent ici. Lorsque la commande est
	 * ind�finie, que le r�sultat de la commande est null (commande qui ne
	 * produit pas d'atome) ou que la commande vise � sortir un argument de la
	 * file (CMD_ARG) on n'enfile pas le r�sultat de l'interpr�tation de cette
	 * commande dans la file d'arguments.
	 */
	@Override
	public void interpret(JarvisInterpreter ji) {
		AbstractAtom res = interpretNoPut(ji);

		if (!(res == null) && !isUndefined && !isArg()) {
			ji.putArg(res);
		}
	}

	/*
	 * Par souci de simplicit�, un �norme switch r�alise l'interpr�tation des
	 * commandes. Une hi�rarchie de classes ferait un meilleur design mais
	 * serait peut-�tre plus obscure. Beau cas de refactoring!
	 */
	@Override
	public AbstractAtom interpretNoPut(JarvisInterpreter ji) {

		// Interpr�tations ill�gales
		if (isUndefined()) {
			throw new UndefinedSymbolException(value);

		} else if (isError()) {
			throw new UndefinedSymbolException(value);

		}

		// Interpr�tation d'atomes
		else if (isList()) {
			return ListAtom.read(ji);
		}

		else if (isNull()) {
			return new NullAtom();
		}

		else if (isTrue()) {
			return new BoolAtom(true);
		} else if (isFalse()) {
			return new BoolAtom(false);
		}

		else if (isDictionnary()) {
			return DictionnaryAtom.read(ji);
		}

		else if (isClosure()) {
			return ClosureAtom.read(ji);
		}

		else if (isString()) {
			return new StringAtom(value.substring(1, value.length() - 1));
		}

		// Pour les types num�riques. Seuls les entiers sont support�s � date.
		// Tout symbole commen�ant par un chiffre est consid�r� comme un nombre
		// et on tentera une traduction ici.
		else if (isNumber()) {

			// V�rifie si c'est un int
			int val;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				ji.outputError("Symbole mal form� ou format non support�: " + value);
				throw new IllegalArgumentException("");

			}

			return new IntAtom(val);
		}

		// Commandes terminales.
		// Ces commandes ne produisent pas d'atomes.
		// Ce sont principalement des commandes sp�ciales pour
		// l'interpr�teur.
		else if (isLoad()) {
			String file = ji.nextInput();

			BufferedReader reader;
			try {

				reader = new BufferedReader(new FileReader(file));
			}

			catch (FileNotFoundException e) {
				ji.output(file+": Nom de fichier incorrect. Veuillez entrer un choix:");
				reader = loadWithMenu(ji, ".\\");
				if (reader == null)
					throw new IllegalArgumentException("Bad filename: " + file);
			}

			loadFromReader(ji,reader);
		}
		
		else if (isLoadMenu()) {
			

			
			BufferedReader reader = loadWithMenu(ji, ".\\");
			
			loadFromReader(ji,reader);
			
		}

		else if (isMute()) {
			ji.setEcho(false);

		}

		else if (isUnMute()) {
			ji.setEcho(true);
		}

		else if (isQuit()) {
			ji.quit();
		}

		else if (isPeek()) {
			if (ji.getArgCount() > 0) {
				ji.output(ji.peekArg().makeKey());
			}
		}

		else if (isArgs()) {
			ji.printArgs();
		}

		else if (isEnv()) {
			ji.printEnvironment();
		} else if (isEnvAll()) {
			ji.printAllEnvironment();
		}

		else if (isDebug()) {
			// Devrait �tre encapsul� chez JarvisInterpreter...
			ji.setReader(new BufferedReader(new InputStreamReader(System.in)));
			ji.setEcho(false);
			ji.setPrompt(true);

		}

		else if (isClear()) {
			ji.clearArgs();
		}

		else if (isUI()) {
			ji.startUI();
		}

		else if (isMessage()) {
			ji.message();
			return null;
		}

		// Cas sp�cial cr�er un nouveau symbole.
		// Faire une r�f�rence avec un symbole existant
		// est l'�quivalent d'une affectation.
		else if (isRef()) {
			String symbol = ji.nextInput();
			if (ji.getEnvironment().getLocal(symbol) != null) {

				/*
				 * Peut �tre une bonne id�e de d�commenter ceci si vous risquez
				 * des affectations sournoises...
				 */
				// ji.output("Attention... symbole existant...");
			}
			AbstractAtom value = ji.getValue();
			ji.getEnvironment().put(symbol, value);

		}

		else if (isEndOfEnvironment()) {
			ji.endOfEnvironment();
		}

		// Cas sp�cial pour sortir un argument de la liste.
		// Il faut �viter de l'interpr�ter dans certains cas.
		else if (isArg()) {
			if (ji.getArgCount() > 0) {
				AbstractAtom atom = ji.getArg();
				if (dontInterpret) {
					return atom;
				}
				if (dontPut) {
					return atom.interpretNoPut(ji);
				} else {
					atom.interpret(ji);
				}

			} else {
				return new NullAtom();
			}
		}

		// Cas final, c'est un symbole (rien d'autre ne match).
		// La r�solution se fait de deux fa�ons diff�rentes.
		// Par d�faut, l'atome r�f�renc� par le symbole est interpr�t�.
		// et peut causer des effets de bord (place des valeurs dans la file
		// d'arguments)
		// C'est ce qui arrive si le symbole est interpr�t� normalement
		// Dans certains cas sp�ciaux, on d�sire obtenir l'atome par retour
		// seulement,
		// comme lorsqu'on fait un envoi de message.
		// Il faut alors activer l'interdiction (dontInterpret) ou d�sactiver
		// l'effet
		// de bord imm�diat de son interpr�tation (dontPut) selon la situation.
		else {
			AbstractAtom atom = ji.getEnvironment().get(value);
			if (atom == null) {

				if (acceptUndefined) {
					isUndefined = true;
					return this;
				} else {
					throw new UndefinedSymbolException("Symbole ind�fini: " + value);
				}

			}

			else { // Symbole trouv�, reste � interpr�ter ce qu'il repr�sente...
				if (dontInterpret) {
					return atom;
				}

				if (dontPut) {
					return atom.interpretNoPut(ji);
				} else {
					atom.interpret(ji);
				}
			}
		}

		// Toutes les commandes qui ne produisent pas des atomes
		// doivent retourner null. Ceci est v�rifi� en amont.
		return null;
	}

	private void loadFromReader(JarvisInterpreter ji, BufferedReader reader) {
		Stack<String> lines = new Stack<String>();
		String line = "";

		line = ji.readSignificantLine(reader);

		while (line != null) {

			lines.push(line);

			line = ji.readSignificantLine(reader);

		}

		while (!lines.empty()) {
			String token = ji.putTokensOnStack(lines.pop());
			ji.pushEval(token);
		}
		
	}

	private BufferedReader loadWithMenu(JarvisInterpreter ji, String path) {
		File file = new File(path);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(file.list()));
		ArrayList<String> choices = new ArrayList<String>();
		for (String name : names) {
			if (!name.startsWith(".") && !name.equals("src") && !name.equals("bin")) {
				choices.add(name);
			}
		}
		BufferedReader reader;
		int i = 1;
		for (String name : choices) {
			ji.output(i + "-" + name);
			i++;
		}
		
		String choice = ji.nextInput();
		int pos;
		try {
			pos = Integer.parseInt(choice);
		} catch (NumberFormatException e) {
			ji.outputError(value+": Choix invalide (entrez une valeur enti�re)");
			return null;
		}
		
		if(!(pos<=choices.size()&&pos>=1)){
			ji.outputError(value+": Choix invalide");
			return null;
		}
		
		choice=choices.get(pos-1);
		file=new File(choice);
		if(file.isDirectory()){
			return loadWithMenu(ji,path+choice);
		}
		
		
		try {

			reader = new BufferedReader(new FileReader(path+"\\"+choice));
		}

		catch (FileNotFoundException e) {
			reader = null;
		}

		return reader;
	}

	private boolean isUI() {
		return value.compareTo(CMD_UI) == 0;
	}

	public boolean isUnMute() {
		return value.compareTo(CMD_UNMUTE) == 0;
	}

	public boolean isLoad() {
		return value.compareTo(CMD_LOAD) == 0;
	}

	@Override
	public String makeKey() {

		return value;
	}

	public CommandAtom(String input) {
		value = input;
	}

	public String getValue() {
		return value;
	}

	// Pr�dicats pour d�terminer le type de commande.
	// All�gent le giga-switch plus haut, n�cessaires pour certaines
	// v�rifications faites de l'ext�rieur.

	public boolean isQuit() {
		return value.compareTo(CMD_QUIT) == 0;
	}

	public boolean isList() {
		return value.compareTo(CMD_LIST) == 0;
	}

	public boolean isEndOfList() {

		return value.compareTo(CMD_ENDOFLIST) == 0;
	}

	public boolean isArgs() {
		return value.compareTo(CMD_ARGS) == 0;
	}

	public boolean isEnv() {
		return value.compareTo(CMD_ENV) == 0;
	}

	public boolean isEnvAll() {
		return value.compareTo(CMD_ENVALL) == 0;
	}
	public boolean isLoadMenu() {
		return value.compareTo(CMD_LOADMENU) == 0;
	}


	public boolean isRef() {
		return value.compareTo(CMD_REF) == 0;
	}

	public boolean isDebug() {
		return value.compareTo(CMD_DEBUG) == 0;
	}

	public boolean isNumber() {
		if (value.length() >= 1) {
			return (value.charAt(0) >= '0') && (value.charAt(0) <= '9');
		}
		return false;
	}

	public boolean isString() {
		if (value.length() >= 2) {
			if ((value.charAt(0) == '"') && (value.charAt(value.length() - 1) == '"')) {
				return true;
			}
		}
		return false;
	}

	public boolean isClear() {
		return value.compareTo(CMD_CLEAR) == 0;
	}

	public boolean isMessage() {
		return value.compareTo(CMD_MESSAGE) == 0;
	}

	public boolean isClosure() {
		return value.compareTo(CMD_CLOSURE) == 0;
	}

	public boolean isEndOfEnvironment() {
		return value.compareTo(CMD_ENDOFENVIRONMENT) == 0;
	}

	public boolean isDictionnary() {
		return value.compareTo(CMD_DICT) == 0;
	}

	public boolean isArg() {
		return value.compareTo(CMD_ARG) == 0;
	}

	public boolean isError() {
		return value.compareTo(CMD_ERROR) == 0;
	}

	public boolean isPeek() {
		return value.compareTo(CMD_PEEK) == 0;
	}

	public boolean isNull() {
		return value.compareTo(CMD_NULL) == 0;
	}

	public boolean isMute() {
		return value.compareTo(CMD_MUTE) == 0;
	}

	public boolean isTrue() {
		return value.compareTo(CMD_TRUE) == 0;
	}

	public boolean isFalse() {
		return value.compareTo(CMD_FALSE) == 0;
	}

	public boolean isEndOfDictionnary() {
		return value.compareTo(CMD_ENDOFDICT) == 0;
	}

	public static void setDontPut(boolean val) {
		dontPut = val;
	}

	public static void setDontInterpret(boolean val) {
		dontInterpret = val;
	}

	public static void setAcceptUndefined(boolean val) {
		acceptUndefined = val;
	}

}
