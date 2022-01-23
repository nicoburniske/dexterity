package nicoburniske.dexterity.components.material

import com.raquo.domtypes.generic.codecs.{BooleanAsIsCodec, StringAsIsCodec}
import com.raquo.laminar.api.L._
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveProp, ReactiveStyle}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

/**
 * mwc-list
 *
 * Lists are continuous, vertical indexes of text or images.
 *
 * ` @material/mwc-list@0.18.0 `
 *
 * @see
 *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
 *   Documentation</a>
 */
object List {

  @js.native
  trait RawElement extends js.Object {

    /**
     * Sets `activated` attribute on selected items which provides a focus-persistent highlight.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `activatable`: Boolean

    /**
     * When `true`, sets `tabindex="0"` on the internal list. Otherwise sets `tabindex="-1"`.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `rootTabbable`: Boolean

    /**
     * When `true`, enables selection of multiple items. This will result in `index` being of type `Set<number>` and selected
     * returning `ListItemBase[]`.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `multi`: Boolean

    /**
     * When `true`, pressing `up` on the keyboard when focused on the first item will focus the last item and `down` when focused
     * on the last item will focus the first item.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `wrapFocus`: Boolean

    /**
     * Determines what `role` attribute to set on all list items.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `itemRoles`: String

    /**
     * Role of the internal `<ul>` element.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `innerRole`: String

    /**
     * When `true`, disables focus and pointer events (thus ripples) on the list. Used for display-only lists.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `noninteractive`: Boolean

    /**
     * All list items that are available for selection. Eligible items have the `[mwc-list-item]` attribute which `ListItemBase`
     * applies automatically.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `items`: Any

    /**
     * Currently-selected list item(s). When `multi` is `true`, `selected` is of type `ListItemBase[]` and when `false`,
     * `selected` is of type `ListItemBase`. `selected` is `null` when no item is selected.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `selected`: Any

    /**
     * Index / indices of selected item(s). When `multi` is `true`, `index` is of type `number` and when `false`, `index` is of
     * type `Set<number>`. Unset indicies are `-1` and empty `Set<number>` for single and multi selection respectively.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def `index`: Any

    /**
     * Selects the elements at the given index / indices.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def select(): Unit

    /**
     * Toggles the selected index, and forcibly selects or deselects the value of `force` if attribtue is provided.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def toggle(): Unit

    /**
     * Returns the index of the currently-focused item. `-1` if none are focused.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def getFocusedItemIndex(): Unit

    /**
     * Focuses the item at the given index and manages tabindex on all other items.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def focusItemAtIndex(): Unit

    /**
     * Resets tabindex on all items and will update items model if provided true. It may be required to call layout if
     * selectability of an element is dynamically changed. e.g. `[mwc-list-item]` attribute is removed from a list item or
     * `noninteractive` is dynamically set on a list item.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def layout(): Unit

  }

  @js.native
  @JSImport("@material/mwc-list", JSImport.Default)
  object RawImport extends js.Object {}

  RawImport // needed because objects are lazy

  type Ref         = dom.html.Element with RawElement
  type El          = ReactiveHtmlElement[Ref]
  type ModFunction = List.type => Mod[El]

  private val tag = new HtmlTag[Ref]("mwc-list", void = false)

  object slots {

    /**
     * Content to display in the lists internal <ul> element.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    def default(els: HtmlElement*): Seq[HtmlElement] = els.map(_.amend(slot := ""))

  }

  /**
   * Sets `activated` attribute on selected items which provides a focus-persistent highlight.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `activatable` = new ReactiveProp("activatable", BooleanAsIsCodec)

  /**
   * When `true`, sets `tabindex="0"` on the internal list. Otherwise sets `tabindex="-1"`.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `rootTabbable` = new ReactiveProp("rootTabbable", BooleanAsIsCodec)

  /**
   * When `true`, enables selection of multiple items. This will result in `index` being of type `Set<number>` and selected
   * returning `ListItemBase[]`.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `multi` = new ReactiveProp("multi", BooleanAsIsCodec)

  /**
   * When `true`, pressing `up` on the keyboard when focused on the first item will focus the last item and `down` when focused on
   * the last item will focus the first item.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `wrapFocus` = new ReactiveProp("wrapFocus", BooleanAsIsCodec)

  /**
   * Determines what `role` attribute to set on all list items.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `itemRoles` = new ReactiveProp("itemRoles", StringAsIsCodec)

  /**
   * Role of the internal `<ul>` element.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `innerRole` = new ReactiveProp("innerRole", StringAsIsCodec)

  /**
   * When `true`, disables focus and pointer events (thus ripples) on the list. Used for display-only lists.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val `noninteractive` = new ReactiveProp("noninteractive", BooleanAsIsCodec)

  /**
   * Fired when a selection has been made via click or keyboard aciton.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val onAction = new EventProp[dom.Event]("action")

  /**
   * Fired when a selection has been made. index is the selected index (will be of type Set<number> if multi and number if
   * single), and diff (of type IndexDiff**) represents the diff of added and removed indices from previous selection.
   *
   * @see
   *   <a href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
   *   Documentation</a>
   */
  val onSelected = new EventProp[dom.Event]("selected")

  object styles {

    import com.raquo.domtypes.generic.keys.Style

    /**
     * Padding before and after the first and last list items.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    val listVerticalPadding = new ReactiveStyle(new Style("--mdc-list-vertical-padding"))

    /**
     * Adjusts the padding of the [padded] list dividers (also propagates to mwc-list-item).
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    val listSidePadding = new ReactiveStyle(new Style("--mdc-list-side-padding"))

    /**
     * Adjusts the left inset padding of an [inset] list divider. Typically used for dividing list items with icons.
     *
     * @see
     *   <a
     *   href="https://github.com/material-components/material-components-web-components/tree/master/packages/tab-bar">Component
     *   Documentation</a>
     */
    val listInsetMargin = new ReactiveStyle(new Style("--mdc-list-inset-margin"))

  }

  def apply(mods: ModFunction*): El = {
    tag(mods.map(_(List)): _*)
  }
}
