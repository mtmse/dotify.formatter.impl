package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.obfl.ExpressionFactory;

/**
 * TODO: Write java doc.
 */
public class OBFLCondition extends OBFLExpressionBase implements Condition {

    public OBFLCondition(String exp, ExpressionFactory ef, MetaVariable... metaVariables) {
        super(exp, ef, metaVariables);
    }

    @Override
    public boolean evaluate() {
        if (exp == null) {
            return true;
        } else {
            return ef.newExpression().evaluate(exp).equals(true);
        }
    }

    @Override
    public boolean evaluate(Context context) {
        if (exp == null) {
            return true;
        } else {
            return ef.newExpression().evaluate(ExpressionTools.resolveVariables(exp, buildArgs(context))).equals(true);
        }
    }

}
