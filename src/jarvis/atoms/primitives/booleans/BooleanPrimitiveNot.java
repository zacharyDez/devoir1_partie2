package jarvis.atoms.primitives.booleans;

import java.util.ArrayList;

import jarvis.atoms.AbstractAtom;
import jarvis.atoms.BoolAtom;
import jarvis.atoms.ObjectAtom;
import jarvis.interpreter.JarvisInterpreter;

//OPERATIONSPRIMITIVES
/*
 * Cette classe implante la partie de l'opération primitive spécifique à la négation.
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

        //Ici, on peut assumer que l'objet qui a reçu le message (self) est un bool et possède donc
        //le champ "value".
        BoolAtom bool1 = (BoolAtom) self.message("value");

        return calculateResult(ji, bool1);

    }

    protected AbstractAtom calculateResult(JarvisInterpreter ji, BoolAtom val1) {

        // C'est ici que l'opération réelle a lieu
        boolean result = !val1.getValue();

        // Ici, construit un objet bool manuellement
        // À noter, on retourne un objet de type bool, pas un atome de type bool.
        ArrayList<AbstractAtom> data = new ArrayList<AbstractAtom>();
        data.add(new BoolAtom(result));

        return new ObjectAtom(((ObjectAtom) ji.getEnvironment().get("bool")), data, ji);
    }

}
