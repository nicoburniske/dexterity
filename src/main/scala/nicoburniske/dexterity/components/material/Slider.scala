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
 * mwc-slider
 *
 * Sliders allow users to make selections from a range of values.
 *
 * {@literal @material/mwc-slider@0.18.0}
 *
 * @see
 *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
 *   Documentation</a>
 */
object Slider {

  @js.native
  trait RawElement extends js.Object {

    /**
     * Current value of the slider.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def `value`: Double

    /**
     * Minimum value of the slider.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def `min`: Double

    /**
     * Maximum value of the slider.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def `max`: Double

    /**
     * When defined, the slider will quantize (round to the nearest multiple) all values to match that step value, except for the
     * minimum and maximum values, which can always be set. When 0, quantization is disabled.<br> **NOTE:** Throws when <0.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def `step`: Double

    /**
     * Shows the thumb pin on a discrete slider.<br> **NOTE:** Numbers displayed inside the slider will be rounded to at most 3
     * decimal digits.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def `pin`: Boolean

    /**
     * Shows the tick marks for each step on the track when the slider is discrete.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def `markers`: Boolean

    /**
     * Recomputes the dimensions and re-lays out the component. This should be called if the dimensions of the slider itself or
     * any of its parent elements change programmatically (it is called automatically on resize and on mousedown / touchstart).
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    def layout(): Unit

  }

  @js.native
  @JSImport("@material/mwc-slider", JSImport.Default)
  object RawImport extends js.Object {}
  RawImport // needed because objects are lazy

  type Ref         = dom.html.Element with RawElement
  type El          = ReactiveHtmlElement[Ref]
  type ModFunction = Slider.type => Mod[El]

  private val tag = new HtmlTag[Ref]("mwc-slider", void = false)

  object slots {}

  /**
   * Current value of the slider.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val `value` = new ReactiveProp("value", DoubleAsIsCodec)

  /**
   * Minimum value of the slider.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val `min` = new ReactiveProp("min", DoubleAsIsCodec)

  /**
   * Maximum value of the slider.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val `max` = new ReactiveProp("max", DoubleAsIsCodec)

  /**
   * When defined, the slider will quantize (round to the nearest multiple) all values to match that step value, except for the
   * minimum and maximum values, which can always be set. When 0, quantization is disabled.<br> **NOTE:** Throws when <0.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val `step` = new ReactiveProp("step", DoubleAsIsCodec)

  /**
   * Shows the thumb pin on a discrete slider.<br> **NOTE:** Numbers displayed inside the slider will be rounded to at most 3
   * decimal digits.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val `pin` = new ReactiveProp("pin", BooleanAsIsCodec)

  /**
   * Shows the tick marks for each step on the track when the slider is discrete.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val `markers` = new ReactiveProp("markers", BooleanAsIsCodec)

  /**
   * Fired when the value changes due to user input. Similar to the input event of the native <input type="range"> element, the
   * input event will not fire when value is modified via JavaScript.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val onInput = new EventProp[dom.Event]("input")

  /**
   * Fired when the value changes and the user has finished interacting with the slider. Similar to the change event of the native
   * <input type="range"> element, the change event will not fire when value is modified via JavaScript.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
   *   Documentation</a>
   */
  val onChange = new EventProp[dom.Event]("change")

  object styles {
    import com.raquo.domtypes.generic.keys.Style

    /**
     * Sets the color of the knob and filled track when slider is active.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    val themeSecondary = new ReactiveStyle(new Style("--mdc-theme-secondary"))

    /**
     * Sets the color of the text in the pin.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    val themeTextPrimaryOnDark = new ReactiveStyle(new Style("--mdc-theme-text-primary-on-dark"))

    /**
     * Sets the color of the circle around the knob on the disabled slider to make it seem cut-out. May be necessary when placing
     * a disabled slider on a different-colored background.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/slider">Component
     *   Documentation</a>
     */
    val sliderBgColorBehindComponent = new ReactiveStyle(new Style("--mdc-slider-bg-color-behind-component"))

  }

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(Slider)): _*)
  }
}
