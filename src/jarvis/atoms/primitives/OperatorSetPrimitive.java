package jarvis.atoms.primitives;

import jarvis.atoms.*;
import jarvis.interpreter.JarvisInterpreter;

public class OperatorSetPrimitive extends PrimitiveOperationAtom {

    /*
     * Triche pour modifier un attribut via un appel de methode en Jarvis
     */
    protected void init() {
        argCount = 2;
    }

    @Override
    protected AbstractAtom execute(JarvisInterpreter ji, ObjectAtom self) {

        // chercher les arguments
        // l'attribut doit etre un StringAtom
        StringAtom attr = (StringAtom) ji.getArg();
        // la valeur peut etre tout Atom
        AbstractAtom val = ji.getArg();

        // la reference a la classe contient les informations sur les attributs
        // la position de l'attribut est trouve avec sa reference de classe
        ListAtom parAttr = (ListAtom) self.getJarvisClass().message("attributes");
        int pos = parAttr.find(attr);

        // values stocker dans un ArrayList<AbstractAtom>
        self.setValue(pos, val);

        // return classReference;
        return val;
    }

    @Override
    public String makeKey() {

        return "OperatorSetPrimitive";
    }

}
