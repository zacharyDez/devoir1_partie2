package jarvis.atoms.primitives.booleans;

import jarvis.atoms.primitives.PrimitiveOperationAtom;

/* OPERATIONSPRIMITIVES
 * Classe de base pour les primitives de la classe bool en Jarvis.
 * Les opérations de base travaillent toutes sur la valeur du BoolAtom qui
 * se trouve dans le champ "value" de chaque objet de type bool en Jarvis.
 * Plusieurs vérifications sont faites ici. Vos nouvelles opérations devraient
 * être des classes descendantes de cette classe.
 *
 * Opérations à implantées:
 * !  (Not)     (BooleanPrimitiveNot)
 * || (Or)      (BooleanPrimitiveOr)
 * && (And)     (BooleanPrimitiveAnd)
 *
 * Remarque: Nous laissons les operateurs dérivés de celui-ci créer leur propre méthode d'éxécution et de calcul.
 * Le nombre d'argument change selon si c'est la négation ou un et/ou.
 */


public abstract class BooleanPrimitiveOperation extends PrimitiveOperationAtom {

    /*
     * Le nombre d'argument pour toutes les opérations dérivées de celle-ci sera de 1 ou 2 (self ou self+1).
     * On doit donc spécifié un nombre d'argument à -1 selon la classe PrimitiveOperationAtom.
     */
    protected void init() {
        argCount = -1;
    }

}
