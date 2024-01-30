package glsl.plugin.utils

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import glsl.GlslTypes
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.psi.interfaces.*


object GlslPsiUtils : GeneratedParserUtilBase() {


    @JvmStatic
    fun primaryExprVariable(builder: PsiBuilder, level: Int): Boolean {
        val isCurrentIdentifier = builder.tokenType == GlslTypes.IDENTIFIER
        val isNextIdentifier = builder.lookAhead(1) == GlslTypes.IDENTIFIER
        if (isCurrentIdentifier && !isNextIdentifier) {
            builder.advanceLexer()
            return true
        }
        return false
    }

    @JvmStatic
    fun ppText(builder: PsiBuilder, level: Int): Boolean {
        while (!builder.eof() && builder.tokenType != GlslTypes.PP_END) {
            builder.advanceLexer()
        }
        return builder.tokenType == GlslTypes.PP_END
    }

    @JvmStatic
    fun ppArg(builder: PsiBuilder, level: Int): Boolean {
        var innerLevel = 0
        while (!builder.eof()) {
            when (builder.tokenType) {
                GlslTypes.COMMA ->
                    if (innerLevel == 0) {
                        return true
                    }

                GlslTypes.LEFT_PAREN ->
                    innerLevel += 1

                GlslTypes.RIGHT_PAREN -> {
                    if (innerLevel == 0) {
                        return true
                    }
                    innerLevel -= 1
                }
            }

            builder.advanceLexer()
        }
        return innerLevel == 0
    }

    /**
     *
     */
    fun getPostfixIdentifier(postfixExpr: GlslPostfixExpr?): GlslIdentifierImpl? {
        return when (postfixExpr) {
            is GlslPrimaryExpr -> postfixExpr.variableIdentifier as? GlslIdentifierImpl
            is GlslFunctionCall -> postfixExpr.variableIdentifier as? GlslIdentifierImpl
            is GlslPostfixArrayIndex -> getPostfixIdentifier(postfixExpr.postfixExpr)
            is GlslPostfixInc -> getPostfixIdentifier(postfixExpr.postfixExpr)
            else -> null
        }
    }
}