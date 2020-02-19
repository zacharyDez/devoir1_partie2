package jarvis.atoms.primitives.booleans;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;

//OPERATIONSPRIMITIVES
/*
 * Cette classe implante la partie de l'op�ration primitive sp�cifique � la n�gation.
 *
 */

public class BooleanPrimitiveNot extends BooleanPrimitiveOperation {

    // Le nombre d'argument doit etre 0 (seulement self)
    protected void init() {
        argCount = 0;
    }

    @Override
    public String makeKey() {

        return "BooleanPrimitiveNot";
    }

    @Override
    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {

        //Ici, on peut assumer que l'objet qui a re�u le message (self) est un bool et poss�de donc
        //le champ "value".
        BoolAtom bool1 = (BoolAtom) self.message("value");

        return calculateResult(ji, bool1);

    }

    protected AbstractAtom calculateResult(JarvisInterpreter ji, BoolAtom val1) {

        // C'est ici que l'op�ration r�elle a lieu
        boolean result = !val1.getValue();

        // Ici, construit un objet bool manuellement
        // � noter, on retourne un objet de type bool, pas un atome de type bool.
        ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
        data.add(new BoolAtom(result));

        return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("bool")), data, ji);
    }

}
