package nicoburniske.dexterity.components.material

import com.raquo.domtypes.generic.codecs._
import com.raquo.laminar.api.L._
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveProp, ReactiveStyle}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
 * mwc-linear-progress
 *
 * Progress indicators express an unspecified wait time or display the length of a process.
 *
 * {@literal @material/mwc-linear-progress@0.18.0}
 *
 * @see
 *   <a
 *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
 *   Documentation</a>
 */
object LinearProgressBar {

  @js.native
  trait RawElement extends js.Object {

    /**
     * Sets the linear-progress into its indeterminate state.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def `indeterminate`: Boolean

    /**
     * Sets the primary progress bar's value. Value should be between [0, 1].
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def `progress`: Double

    /**
     * Sets the buffer progress bar's value. Value should be between [0, 1]. Setting this value to be less than 1 will reveal
     * moving, buffering dots.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def `buffer`: Double

    /**
     * Reverses the direction of the linear progress indicator.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def `reverse`: Boolean

    /**
     * Sets the progress indicator to the closed state. Sets content opactiy to 0. Typically should be set to true when loading
     * has finished.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def `closed`: Boolean

    /**
     * Sets LinearProgress.closed to false
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def open(): Unit

    /**
     * Sets LinearProgress.closed to true
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    def close(): Unit

  }

  @js.native
  @JSImport("@material/mwc-linear-progress", JSImport.Default)
  object RawImport extends js.Object {}
  RawImport // needed because objects are lazy

  type Ref         = dom.html.Element with RawElement
  type El          = ReactiveHtmlElement[Ref]
  type ModFunction = LinearProgressBar.type => Mod[El]

  private val tag = new HtmlTag[Ref]("mwc-linear-progress", void = false)

  object slots {}

  /**
   * Sets the linear-progress into its indeterminate state.
   *
   * @see
   *   <a
   *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
   *   Documentation</a>
   */
  val `indeterminate` = new ReactiveProp("indeterminate", BooleanAsIsCodec)

  /**
   * Sets the primary progress bar's value. Value should be between [0, 1].
   *
   * @see
   *   <a
   *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
   *   Documentation</a>
   */
  val `progress` = new ReactiveProp("progress", DoubleAsIsCodec)

  /**
   * Sets the buffer progress bar's value. Value should be between [0, 1]. Setting this value to be less than 1 will reveal
   * moving, buffering dots.
   *
   * @see
   *   <a
   *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
   *   Documentation</a>
   */
  val `buffer` = new ReactiveProp("buffer", DoubleAsIsCodec)

  /**
   * Reverses the direction of the linear progress indicator.
   *
   * @see
   *   <a
   *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
   *   Documentation</a>
   */
  val `reverse` = new ReactiveProp("reverse", BooleanAsIsCodec)

  /**
   * Sets the progress indicator to the closed state. Sets content opactiy to 0. Typically should be set to true when loading has
   * finished.
   *
   * @see
   *   <a
   *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
   *   Documentation</a>
   */
  val `closed` = new ReactiveProp("closed", BooleanAsIsCodec)

  object styles {
    import com.raquo.domtypes.generic.keys.Style

    /**
     * Sets the color of primary progress bar.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    val themePrimary = new ReactiveStyle(new Style("--mdc-theme-primary"))

    /**
     * Sets the color of the buffer progress bar.<br> **NOTE:** to change the color of the buffering dots, you must do so in the
     * image of `--mdc-linear-progress-buffering-dots-image`.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    val linearProgressBufferColor = new ReactiveStyle(new Style("--mdc-linear-progress-buffer-color"))

    /**
     * Sets the image to use as the buffering dots. This pattern is then repeated horizontally and animated.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/linear-progress">Component
     *   Documentation</a>
     */
    val linearProgressBufferingDotsImage = new ReactiveStyle(new Style("--mdc-linear-progress-buffering-dots-image"))

  }

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(LinearProgressBar)): _*)
  }

}
