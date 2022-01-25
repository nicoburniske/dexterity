package nicoburniske.dexterity.components.material

import com.raquo.domtypes.generic.codecs._
import com.raquo.laminar.api.L._
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveProp, ReactiveStyle}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


/** mwc-circular-progress
 *
 * Progress indicators express an unspecified wait time or display the length of a process.
 *
 * {@literal @material/mwc-circular-progress@0.18.0}
 *
 * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
 */
object CircularProgress {

  @js.native
  trait RawElement extends js.Object {

    /** Sets the circular-progress into its indeterminate state.
     *
     * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
     */
    def `indeterminate`: Boolean

    /** Sets the progress bar's value. Value should be between [0, 1].
     *
     * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
     */
    def `progress`: Double

    /** Sets the progress indicator's sizing based on density scale. Minimum value is `-8`. Each unit change in density scale corresponds to 4px change in side dimensions. The stroke width adjusts automatically.
     *
     * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
     */
    def `density`: Double

    /** Sets the progress indicator to the closed state. Sets content opacity to 0. Typically should be set to true when loading has finished.
     *
     * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
     */
    def `closed`: Boolean

    /** Sets CircularProgress.closed to false
     *
     * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
     */
    def open(): Unit

    /** Sets CircularProgress.closed to true
     *
     * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
     */
    def close(): Unit

  }

  @js.native
  @JSImport("@material/mwc-circular-progress", JSImport.Default)
  object RawImport extends js.Object {}
  RawImport // needed because objects are lazy

  type Ref         = dom.html.Element with RawElement
  type El          = ReactiveHtmlElement[Ref]
  type ModFunction = CircularProgress.type => Mod[El]

  private val tag = new HtmlTag[Ref]("mwc-circular-progress", void = false)

  object slots {}

  /** Sets the circular-progress into its indeterminate state.
   *
   * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
   */
  val `indeterminate` = new ReactiveProp("indeterminate", BooleanAsIsCodec)

  /** Sets the progress bar's value. Value should be between [0, 1].
   *
   * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
   */
  val `progress` = new ReactiveProp("progress", DoubleAsIsCodec)

  /** Sets the progress indicator's sizing based on density scale. Minimum value is `-8`. Each unit change in density scale corresponds to 4px change in side dimensions. The stroke width adjusts automatically.
   *
   * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
   */
  val `density` = new ReactiveProp("density", DoubleAsIsCodec)

  /** Sets the progress indicator to the closed state. Sets content opacity to 0. Typically should be set to true when loading has finished.
   *
   * @see <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/circular-progress">Component Documentation</a>
   */
  val `closed` = new ReactiveProp("closed", BooleanAsIsCodec)

  object styles {
    import com.raquo.domtypes.generic.keys.Style

  }

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(CircularProgress)): _*)
  }
}