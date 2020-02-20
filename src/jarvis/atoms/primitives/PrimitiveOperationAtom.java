package jarvis.atoms.primitives;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;

public abstract class PrimitiveOperationAtom extends AbstractAtom {

    protected int argCount;

    protected abstract AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self);

    //OPERATIONSPRIMITIVES
    /*
     * Gabarit pour les fonctions tricheuses.
     * La plupart ont un nombre fixe de param�tres, il est donc v�rifi� ici.
     * Dans certains cas, on veut laisser cette responsabilit� � la sous-classe.
     * Celle-ci sp�cifiera donc un nombre de param�tres � -1 dans sa fonction init().
     *
     */

    public PrimitiveOperationAtom() {
        init();
    }

    protected abstract void init();

    @Override
    public AbstractAtom interpretNoPut(JarvisInterpreter ji) {

        if (argCount != ji.getArgCount() && argCount != -1) {
            throw new IllegalArgumentException("Primitive " + ji.getEnvironment().reverseLookup(this) + ": Bad number of arguments, expected " + argCount
                    + " got " + (ji.getArgCount()));
        }

        //Les fonctions tricheuses sont toujours des m�thodes. Elles ne doivent pas �tre
        //appel�es autrement que par l'envoi d'un message � un objet.
        //Ainsi, le symbole self devrait �tre d�fini lorsqu'une telle fonction est appel�e.
        ObjectAtom self = (ObjectAtom) ji.getEnvironment().get("self");


        AbstractAtom res = execute(ji, self);

        return res;

    }

}
