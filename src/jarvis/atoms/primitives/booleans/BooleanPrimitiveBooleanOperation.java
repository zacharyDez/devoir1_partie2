package jarvis.atoms.primitives.booleans;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;


/* OPERATIONSPRIMITIVES
 * Classe de base pour les primitives de la classe bool en Jarvis.
 * Les op�rations de base travaillent toutes sur la valeur du BoolAtom qui
 * se trouve dans le champ "value" de chaque objet de type bool en Jarvis.
 * Plusieurs v�rifications sont faites ici. Vos nouvelles op�rations devraient
 * �tre des classes descendantes de cette classe.
 *
 * Op�rations � derivees:
 * || (Or)      (BooleanPrimitiveOr)
 * && (And)     (BooleanPrimitiveAnd)
 *
 * Remarque: Nous laissons les operateurs d�riv�s de celui-ci cr�er leur propre m�thode d'�x�cution et de calcul.
 * Le nombre d'argument change selon si c'est la n�gation ou un et/ou.
 */


public abstract class BooleanPrimitiveBooleanOperation extends BooleanPrimitiveOperation {

    // Le nombre d'argument doit etre de un (self et 1 autre)
    protected void init() {
        argCount = 1;
    }

    protected abstract AbstractAtom calculateResult(JarvisInterpreter ji, BoolAtom val1, BoolAtom val2);

    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {
        //Ici, on peut assumer que l'objet qui a re�u le message (self) est un bool et poss�de donc
        //le champ "value".
        BoolAtom bool1 = (BoolAtom) self.message("value");

        //Le second argument est pris de la file d'arguments. Il peut avoir n'importe quelle forme.
        AbstractAtom arg2 = ji.getArg();
        BoolAtom bool2;

        if (arg2 instanceof BoolAtom) {
            //Si l'argument est de type BoolAtom, alors l'op�ration se fera directement avec lui.
            bool2 = (BoolAtom) arg2;
        } else {
            //Sinon, il faut v�rifier si on a obtenu un objet jarvis.
            if (arg2 instanceof ObjectAtom) {

                //Si c'est un objet de type bool, alors il devrait avoir un champ "value".
                AbstractAtom res = ((ObjectAtom) arg2).message("value");

                //Si le champ "value" existe et s'il contient bien un BoolAtom, l'op�ration se fera avec lui.
                if (res instanceof BoolAtom) {
                    bool2 = (BoolAtom) res;
                }
                //Si le champ n'existe pas ou qu'il ne contient pas le bon type d'atome, alors on ne peut pas continuer
                else
                    throw new IllegalArgumentException(makeKey() + ", argument 2: object does not contain a \"value\" field of type BoolAtom. Class = " + ((ObjectAtom) arg2).findClassName(ji) + ", object contents = [" + arg2.makeKey() + "]");
            }
            //Si ce n'est pas un BoolAtom ou un ObjectAtom, �a ne peut pas �tre le bon type d'argument.
            else
                throw new IllegalArgumentException(makeKey() + ", argument 2: wrong atom type " + arg2.getClass().getName() + ", value = " + arg2.makeKey());
        }
        //Proc�de au calcul de la negation de la primitive concernant
        return calculateResult(ji, bool1, bool2);
    }
}
