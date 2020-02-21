package jarvis.atoms;

import com.sun.org.apache.xpath.internal.operations.Bool;
import jarvis.interpreter.JarvisInterpreter;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/*
 * Cette classe implante l'objet de base.
 * L'interpr�teur comprend un objet comme
 * une simple liste de valeurs.
 * L'organisation de ses donn�es est sp�cifi�e
 * par la classe. Celle-ci peut �tre retrouv�e
 * via le lien classReference.
 */
public class ObjectAtom extends AbstractAtom {

    /*
     * Si vous ajoutez des champs � JarvisClass
     * ces constantes doivent le refl�ter.
     * Elles sont utilis�es pour retrouver
     * les membres d'une classe.
     *
     */
    public static final int ATTRIBUTE_FIELD = 0;
    public static final int METHOD_FIELD = 1;
    public static final int PARENT_FIELD = 2;

    /*
     * R�f�rence � la classe de cet objet.
     */
    private ObjectAtom classReference;
    private ArrayList<AbstractAtom> values;

    //R�f�rence utile pour faire des reverse lookup
    private JarvisInterpreter ji;


    // Constructeur d'objet g�n�rique
    // Utilis� comme raccourci par les fonctions tricheuses.
    public ObjectAtom(ObjectAtom theClass, ArrayList<AbstractAtom> vals, JarvisInterpreter ji) {

        classReference = theClass;

        values = new ArrayList<AbstractAtom>();
        values.addAll(vals);

        this.ji = ji;
    }

    @Override
    public AbstractAtom interpretNoPut(JarvisInterpreter ji) {
        return this;
    }

    public ObjectAtom getJarvisClass() {
        return classReference;
    }


    //Cas sp�cial o� le selecteur n'est pas encore encapsul� dans un atome
    //Support� pour all�ger la syntaxe.
    public AbstractAtom message(String selector) {

        return message(new StringAtom(selector));

    }

    //H�RITAGE
    //VARIABLESCLASSE
    /*
     * Algorithme de gestion des messages.
     * Ce bout de code a pour responsabilit� de d�terminer si le message
     * concerne un attribut ou une m�thode.
     * Pour implanter l'h�ritage, cet algorithme doit n�cessairement �tre modifi�.
     */
    public AbstractAtom message(AbstractAtom selector) {
        ArrayList<ObjectAtom> parents = new ArrayList<ObjectAtom>();
        parents = getParentsArray();


        System.out.println("--------");
        System.out.println("Parents Size: ");
        System.out.println(parents.size());
        System.out.println("--------");



        // ajouter element courant dans la liste d'objets
        parents.add(0, classReference);

        ListAtom members = getAttributes(parents);
        for(int i=0; i<members.size(); i++){
            StringAtom m = (StringAtom) members.get(i);
        }

        //V�rifie si c'est un attribut
        int pos = members.find(selector);

        if (pos == -1) {
            AbstractAtom res = classReference.findMethod(selector);
            return res;
        } else {
            //C'est un attribut.
            return values.get(pos);
        }
    }

    private ArrayList<ObjectAtom> getParentsArray() {

        ArrayList<ObjectAtom> parents = new ArrayList<ObjectAtom>();
        AbstractAtom nextObj = new NullAtom();
        try {
            nextObj = values.get(PARENT_FIELD);
        } catch (IndexOutOfBoundsException e){
            // pass
        }

        while (!(nextObj instanceof NullAtom)) {
            parents.add((ObjectAtom) nextObj);
            nextObj = ((ObjectAtom) nextObj).values.get(PARENT_FIELD);
        }
        return parents;
    }

    private ListAtom getAttributes(ArrayList<ObjectAtom> parents){
        ListAtom totalAttributes = (ListAtom) parents.get(parents.size()-1).values.get(ATTRIBUTE_FIELD);

        for (int i = parents.size()-1; i < 0; i--) {
            //Va chercher les attributs
            ListAtom members = (ListAtom) parents.get(i).values.get(ATTRIBUTE_FIELD);
            for(int j=0; j<members.size(); j++){
                totalAttributes.add(members.get(j));
            }
        }

        return totalAttributes;
    }


    private AbstractAtom findMethod(AbstractAtom selector) {
        // pas un attribut...
        // Va chercher les m�thodes
        DictionnaryAtom methods = (DictionnaryAtom) values.get(METHOD_FIELD);

        // Cherche dans le dictionnaire
        AbstractAtom res = methods.get(selector.makeKey());

        if (res == null) {

            if (values.get(PARENT_FIELD) instanceof NullAtom) {
                return new StringAtom("ComprendPas" + selector);
            } else {
                return ((ObjectAtom) values.get(PARENT_FIELD)).findMethod(selector);
            }
        }

        return res;

    }

    public void setClass(ObjectAtom theClass) {
        classReference = theClass;
    }


    //Surtout utile pour l'affichage dans ce cas-ci...
    @Override
    public String makeKey() {
        String s = "";
        int i = 0;

        s += "\"" + ji.getEnvironment().reverseLookup(classReference) + "\":";

        for (AbstractAtom atom : values) {

            s += " " + ((ListAtom) classReference.values.get(0)).get(i).makeKey() + ":";
            if (atom instanceof ClosureAtom) {
                s += atom;
            } else {
                s += atom.makeKey();
            }

            i++;
        }

        return s;
    }

    public String findClassName(JarvisInterpreter ji) {

        return ji.getEnvironment().reverseLookup(classReference);

    }

    public void setValue(int idx, AbstractAtom val) {
        values.set(idx, val);
    }
}
