package jarvis.atoms.primitives.booleans;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;


/* OPERATIONSPRIMITIVES
 * Classe de base pour les primitives de la classe bool en Jarvis.
 * Les opérations de base travaillent toutes sur la valeur du BoolAtom qui
 * se trouve dans le champ "value" de chaque objet de type bool en Jarvis.
 * Plusieurs vérifications sont faites ici. Vos nouvelles opérations devraient
 * être des classes descendantes de cette classe.
 *
 * Opérations à derivees:
 * || (Or)      (BooleanPrimitiveOr)
 * && (And)     (BooleanPrimitiveAnd)
 *
 * Remarque: Nous laissons les operateurs dérivés de celui-ci créer leur propre méthode d'éxécution et de calcul.
 * Le nombre d'argument change selon si c'est la négation ou un et/ou.
 */


public abstract class BooleanPrimitiveBooleanOperation extends BooleanPrimitiveOperation {

    // Le nombre d'argument doit etre de un (self et 1 autre)
    protected void init() {
        argCount = 1;
    }

    protected abstract AbstractAtom calculateResult(JarvisInterpreter ji, BoolAtom val1, BoolAtom val2);

    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {
        //Ici, on peut assumer que l'objet qui a reçu le message (self) est un bool et possède donc
        //le champ "value".
        BoolAtom bool1 = (BoolAtom) self.message("value");

        //Le second argument est pris de la file d'arguments. Il peut avoir n'importe quelle forme.
        AbstractAtom arg2 = ji.getArg();
        BoolAtom bool2;

        if (arg2 instanceof BoolAtom) {
            //Si l'argument est de type BoolAtom, alors l'opération se fera directement avec lui.
            bool2 = (BoolAtom) arg2;
        } else {
            //Sinon, il faut vérifier si on a obtenu un objet jarvis.
            if (arg2 instanceof ObjectAtom) {

                //Si c'est un objet de type bool, alors il devrait avoir un champ "value".
                AbstractAtom res = ((ObjectAtom) arg2).message("value");

                //Si le champ "value" existe et s'il contient bien un BoolAtom, l'opération se fera avec lui.
                if (res instanceof BoolAtom) {
                    bool2 = (BoolAtom) res;
                }
                //Si le champ n'existe pas ou qu'il ne contient pas le bon type d'atome, alors on ne peut pas continuer
                else
                    throw new IllegalArgumentException(makeKey() + ", argument 2: object does not contain a \"value\" field of type BoolAtom. Class = " + ((ObjectAtom) arg2).findClassName(ji) + ", object contents = [" + arg2.makeKey() + "]");
            }
            //Si ce n'est pas un BoolAtom ou un ObjectAtom, ça ne peut pas être le bon type d'argument.
            else
                throw new IllegalArgumentException(makeKey() + ", argument 2: wrong atom type " + arg2.getClass().getName() + ", value = " + arg2.makeKey());
        }
        //Procède au calcul de la negation de la primitive concernant
        return calculateResult(ji, bool1, bool2);
    }
}
