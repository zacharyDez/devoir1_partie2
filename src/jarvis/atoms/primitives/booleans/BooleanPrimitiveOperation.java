package jarvis.atoms.primitives.booleans;

import jarvis.atoms.primitives.PrimitiveOperationAtom;

/* OPERATIONSPRIMITIVES
 * Classe de base pour les primitives de la classe bool en Jarvis.
 * Les op�rations de base travaillent toutes sur la valeur du BoolAtom qui
 * se trouve dans le champ "value" de chaque objet de type bool en Jarvis.
 * Plusieurs v�rifications sont faites ici. Vos nouvelles op�rations devraient
 * �tre des classes descendantes de cette classe.
 *
 * Op�rations � implant�es:
 * !  (Not)     (BooleanPrimitiveNot)
 * || (Or)      (BooleanPrimitiveOr)
 * && (And)     (BooleanPrimitiveAnd)
 *
 * Remarque: Nous laissons les operateurs d�riv�s de celui-ci cr�er leur propre m�thode d'�x�cution et de calcul.
 * Le nombre d'argument change selon si c'est la n�gation ou un et/ou.
 */


public abstract class BooleanPrimitiveOperation extends PrimitiveOperationAtom {

    /*
     * Le nombre d'argument pour toutes les op�rations d�riv�es de celle-ci sera de 1 ou 2 (self ou self+1).
     * On doit donc sp�cifi� un nombre d'argument � -1 selon la classe PrimitiveOperationAtom.
     */
    protected void init() {
        argCount = -1;
    }

}
