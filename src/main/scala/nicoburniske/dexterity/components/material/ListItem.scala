package nicoburniske.dexterity.components.material

import com.raquo.domtypes.generic.codecs.{BooleanAsIsCodec, DoubleAsIsCodec, StringAsIsCodec}
import com.raquo.domtypes.generic.keys.EventProp
import com.raquo.laminar.api.L._
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveProp, ReactiveStyle}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
 * mwc-list-item
 *
 * List item
 *
 * {@literal @material/mwc-list@0.18.0}
 *
 * @see
 *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
 *   Documentation</a>
 */
object ListItem {

  @js.native
  trait RawElement extends js.Object {

    /**
     * Value associated with this list item (used by mwc-select).
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `value`: String

    /**
     * Used to group items together (used by mwc-menu for menu selection groups and mwc-radio-list-element).
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `group`: String

    /**
     * Reflects tabindex and sets internal tab indices.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `tabindex`: Double

    /**
     * Reflects disabled and sets internal disabled attributes.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `disabled`: Boolean

    /**
     * Activates the two-line variant and enables the secondary slot.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `twoline`: Boolean

    /**
     * Activates focus-persistent ripple.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `activated`: Boolean

    /**
     * Determines which graphic layout to show and enables the graphic slot.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `graphic`: Any

    /**
     * Allows arbitrary width for multiple slotted graphics.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `multipleGraphics`: Boolean

    /**
     * Activates the meta layout tile and enables the meta slot.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `hasMeta`: Boolean

    /**
     * Disables focus and pointer events for the list item.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `noninteractive`: Boolean

    /**
     * Denotes that the list item is selected.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `selected`: Boolean

    /**
     * Trimmed textContent of the list item.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def `text`: String

  }

  @js.native
  @JSImport("@material/mwc-list/mwc-list-item", JSImport.Default)
  object RawImport extends js.Object {}
  RawImport // needed because objects are lazy

  type Ref         = dom.html.Element with RawElement
  type El          = ReactiveHtmlElement[Ref]
  type ModFunction = ListItem.type => Mod[El]

  private val tag = new HtmlTag[Ref]("mwc-list-item", void = false)

  object slots {

    /**
     * Primary text to display in the list item. Note, text must be wrapped in an inline node to be styled for disabled variant.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def default(els: HtmlElement*): Seq[HtmlElement] = els.map(_.amend(slot := ""))

    /**
     * First tile graphic to display when graphic attribute is defined.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def graphic(els: HtmlElement*): Seq[HtmlElement] = els.map(_.amend(slot := "graphic"))

    /**
     * Last tile meta icon or text to display when hasMeta is true.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def meta(els: HtmlElement*): Seq[HtmlElement] = els.map(_.amend(slot := "meta"))

    /**
     * Secondary text displayed below primary text of a two-line list item.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    def secondary(els: HtmlElement*): Seq[HtmlElement] = els.map(_.amend(slot := "secondary"))

  }

  /**
   * Value associated with this list item (used by mwc-select).
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `value` = new ReactiveProp("value", StringAsIsCodec)

  /**
   * Used to group items together (used by mwc-menu for menu selection groups and mwc-radio-list-element).
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `group` = new ReactiveProp("group", StringAsIsCodec)

  /**
   * Reflects tabindex and sets internal tab indices.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `tabindex` = new ReactiveProp("tabindex", DoubleAsIsCodec)

  /**
   * Reflects disabled and sets internal disabled attributes.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `disabled` = new ReactiveProp("disabled", BooleanAsIsCodec)

  /**
   * Activates the two-line variant and enables the secondary slot.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `twoline` = new ReactiveProp("twoline", BooleanAsIsCodec)

  /**
   * Activates focus-persistent ripple.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `activated` = new ReactiveProp("activated", BooleanAsIsCodec)

  /**
   * Allows arbitrary width for multiple slotted graphics.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `multipleGraphics` = new ReactiveProp("multipleGraphics", BooleanAsIsCodec)

  /**
   * Activates the meta layout tile and enables the meta slot.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `hasMeta` = new ReactiveProp("hasMeta", BooleanAsIsCodec)

  /**
   * Disables focus and pointer events for the list item.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `noninteractive` = new ReactiveProp("noninteractive", BooleanAsIsCodec)

  /**
   * Denotes that the list item is selected.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `selected` = new ReactiveProp("selected", BooleanAsIsCodec)

  /**
   * Trimmed textContent of the list item.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val `text` = new ReactiveProp("text", StringAsIsCodec)

  /**
   * Fired upon click and when selected property is changed. Requests selection from the mwc-list.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
   *   Documentation</a>
   */
  val onRequestSelected = new EventProp[dom.Event]("request-selected")

  object styles {
    import com.raquo.domtypes.generic.keys.Style

    /**
     * Line height of the meta icon or text and width & height of the slotted parent wrapper.
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    val listItemMetaSize = new ReactiveStyle(new Style("--mdc-list-item-meta-size"))

    /**
     * Line height of the graphic and width & height of the slotted parent wrapper. 24px when graphic is "icon". 40px when graphic
     * is "avatar". 56px when graphic is "medium", and "large".
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    val listItemGraphicSize = new ReactiveStyle(new Style("--mdc-list-item-graphic-size"))

    /**
     * Margin between the text and graphic. 16px when graphic is "avatar", "medium", "large", and "control". 32px when graphic is
     * "icon".
     *
     * @see
     *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/list">Component
     *   Documentation</a>
     */
    val listItemGraphicMargin = new ReactiveStyle(new Style("--mdc-list-item-graphic-margin"))

  }

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(ListItem)): _*)
  }

}
