package jarvis.atoms;

import jarvis.interpreter.JarvisInterpreter;

//SOUSLECAPOT
/*
 * Un atome est une unit� de base reconnue par l'interpr�teur.
 * Les atomes de base sont:
 *  (les types primitif)
 *  entiers - IntAtom
 *  bool�ens - BoolAtom
 *  liste - ListAtom
 *  table de hashage - DictionnaryAtom
 *  null - NullAtom
 *  objet - ObjectAtom
 *  string - StringAtom
 *  
 *  L'interpr�tation de ces atomes ne produit aucun changement notable,
 *  autre que de les mettre dans la file d'arguments de l'interpr�teur
 *  dans certains cas.
 *  
 *  De plus, le type ClosureAtom implante la fermeture, 
 *  une suite de commandes Jarvis qui s'ex�cutent dans leur environnement
 *  avec des param�tres.
 *  
 *  L'interpr�tation d'une fermeture cr�e un environnement, lie les 
 *  arguments aux param�tres et empile les commandes sur la pile de 
 *  l'interpr�teur, ce qui ressemble � l'�valuation d'une fonction.
 *  
 *  Finalement, CommandAtom repr�sente toute entr�e non interpr�t�e.
 *  L'interpr�tation d'un atome de ce type a habituellement un effet
 *  sur l'interpr�teur et son environnement, comme faire des commandes
 *  sp�ciales ou fabriquer d'autres atomes.
 * 
 * 
 */

public abstract class AbstractAtom {
	
	protected boolean isUndefined=false;

	/*
	 * Interpr�tation d'un atome.
	 * Le code commun � tous les atomes.
	 */
	public void interpret(JarvisInterpreter ji) {
		AbstractAtom res = interpretNoPut(ji);
		
		/*
		 * Enfiler le r�sultat d'interpr�tation dans la file d'arguments sert
		 * � pr�parer des arguments pour un appel de m�thode ou de closure.
		 * tout simplement en �valuant des variables ou des nouvelles valeurs.
		 * Les �nonc�s qui font �valuer des atomes enfilent les r�sultats 
		 * dans la file d'arguments.
		 */
		ji.putArg(res);
		
	}

	/*
	 * Chaque type d'atome a son interpr�tation diff�rente.
	 * Les types les plus simples se retournent eux-m�mes (atomes indivisibles...)
	 * Le type ClosureAtom lance l'interpr�tation du code qu'il contient. 
	 */
	public abstract AbstractAtom interpretNoPut(JarvisInterpreter ji);

	public final void print(JarvisInterpreter ji) {
		ji.output(makeKey());
	}
	
	/*
	 * Pr�sentement utilis� seulement par CommandAtom.
	 * Reste ici pour �viter des tentatives de coercitions 
	 * et tests de typage qui alourdiraient le code. 
	 * Potentiellement utile pour d'autres atomes dans le futur.
	 */
	public boolean isUndefined()
	{
		return isUndefined;
	}

	/*
	 * Transforme l'atome en string.
	 * toString demeure intacte pour obtenir l'affichage
	 * d'objets standard dans certains cas.
	 * Sert aussi � fabriquer des cl�s � partir d'atomes
	 * pour chercher dans un dictionnaire ou une liste.
	 * C'est ce m�canisme qui permet d'implanter les conditionnelles
	 * en conjonction avec la passation de messages, si le s�lecteur
	 * est un atome au lieu d'un symbole.
	 */
	public abstract String makeKey();

}
