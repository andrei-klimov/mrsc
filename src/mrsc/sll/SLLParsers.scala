package mrsc.sll

import scala.util.parsing.combinator.ImplicitConversions
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.input.{CharSequenceReader => Reader}

object SLLParsers extends StandardTokenParsers with ImplicitConversions {
  lexical.delimiters += ("(", ")", ",", "=", ";")
  def prog = definition+
  def definition: Parser[Def] = gFun | fFun
  def term: Parser[Expr] = fcall | gcall | ctr | vrb
  def uid = ident ^? {case id if id.charAt(0).isUpper => id}
  def lid = ident ^? {case id if id.charAt(0).isLower => id}
  def fid = ident ^? {case id if id.charAt(0) == 'f' => id}
  def gid = ident ^? {case id if id.charAt(0) == 'g' => id}
  def vrb = lid ^^ Var
  def pat = uid ~ ("(" ~> repsep(vrb, ",") <~ ")") ^^ Pat
  def fFun = fid ~ ("(" ~> repsep(vrb, ",") <~ ")") ~ ("=" ~> term <~ ";") ^^ FFun
  def gFun = 
    gid ~ ("(" ~> pat) ~ ((("," ~> vrb)*) <~ ")") ~ ("=" ~> term <~ ";") ^^ GFun
  def ctr = uid ~ ("(" ~> repsep(term, ",") <~ ")") ^^ Ctr
  def fcall = fid ~ ("(" ~> repsep(term, ",") <~ ")") ^^ FCall
  def gcall = gid ~ ("(" ~> repsep(term, ",") <~ ")") ^^ GCall
  def parseProg(s: String) = Program(prog(new lexical.Scanner(new Reader(s))).get)
  def parseExpr(s: String) = term(new lexical.Scanner(new Reader(s))).get
}