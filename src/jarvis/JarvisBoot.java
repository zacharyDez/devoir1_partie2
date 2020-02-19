package jarvis;

import jarvis.interpreter.JarvisInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

/*
 * Bienvenue dans Jarvis!
 * Ne regardez pas trop cette classe, ce n'est qu'un
 * petit demarrage pour l'interpreteur.
 * Si vous voulez le forcer a charger un fichier en particulier
 * vous pouvez le faire via les arguments a l'execution:
 * Run->configurations, onglet arguments, entrez le nom du fichier
 * a cet endroit.
 * 
 * Pour retrouver les sections de code a modifier par theme,
 * double-cliquez sur les mots suivants et cherchez-les dans
 * votre espace de travail a l'aide de CTRL-ALT-G.
 * OPERATIONSPRIMITIVES
 * RECTANGLE
 * HERITAGE
 * MUTATEUR
 * VARIABLESCLASSE 
 * 
 * Pour comprendre un peu mieux le fonctionnement
 * de Jarvis, vous pouvez chercher le theme suivant:
 * SOUSLECAPOT
 * 
 */

public class JarvisBoot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean echo = false;
		boolean prompt = true;
		boolean floaded = false;
		BufferedReader reader;
		if (args.length == 1) {

			String file = args[0];

			try {
				println("Attempting to load " + file + "...");
				reader = new BufferedReader(new FileReader(file));
				echo = true;
				prompt = false;
				floaded = true;
			}

			catch (FileNotFoundException e) {
				println("File not found. Reading from console...");
				reader = new BufferedReader(new InputStreamReader(System.in));
			}
		} else {
			reader = new BufferedReader(new InputStreamReader(System.in));
		}

		JarvisInterpreter interpreter = new JarvisInterpreter(reader, echo, prompt, floaded);

		println("Jarvis 0.0314159");

		interpreter.run();

	}

	public static void print(Object obj) {

		System.out.print(obj);

	}

	public static void println(Object obj) {

		System.out.println(obj);
	}

}
