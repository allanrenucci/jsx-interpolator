package jsx.internal

object Hole {
  // withing private use area
  private final val HoleStart = '\uE000'
  private final val HoleChar  = '\uE001'

  def isHoleStart(ch: Char) = ch == HoleStart
  def isHoleChar(ch: Char) = isHoleStart(ch) || ch == HoleChar

  def encode(i: Int): String = HoleStart + HoleChar.toString * i
  def decode(cs: String): Int = cs.takeWhile(isHoleChar).length - 1
}
