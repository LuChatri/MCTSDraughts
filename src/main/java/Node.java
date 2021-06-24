import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a node in a multifurcating (non-binary) tree.
 *
 * Many methods of this class return objects of type T. T is the
 * concrete class; the node returns a reference to itself for
 * convenient method chaining.
 *
 * @param <T> The concrete class extending this class (e.g. MyCustomNode).
 * @param <D> Data type associated with the node (e.g. Integer, MyCustomNodeData).
 */
public abstract class Node<T extends Node<T, D>, D> {

    private T parent;
    private List<T> children;
    private D data;

    /**
     * Constructs a node.
     *
     * Note that children should be mutable (not from Arrays.asList or
     * the like) if you plan to call methods like {@link #addChild(Node)}.
     *
     * @param parent Node one step higher in the tree.
     * @param children List of nodes one step lower in the tree.
     * @param data Extra information associated with this node.
     */
    public Node(T parent, List<T> children, D data) {
        setParent(parent);
        setChildren(children);
        setData(data);
    }

    /**
     * Should return 'this.'
     *
     * This method allows method chaining in concrete Node classes using
     * non-inherited methods of the concrete class (for example, a call
     * like myCustomNode.getParent().myCustomNodeMethod())
     *
     * @return this
     */
    public abstract T getThis();

    /**
     * Checks if this node is the root node.
     *
     * @return Whether this node lacks a parent.
     */
    public boolean isRoot() {
        return getParent() == null;
    }

    /**
     * Checks if this node is a leaf node.
     *
     * @return Whether this node lacks children.
     */
    public boolean isLeaf() {
        return getChildren().size() == 0;
    }

    public T getParent() {
        return parent;
    }

    public T setParent(T parent) {
        this.parent = parent;
        return getThis();
    }

    public List<T> getChildren() {
        return children;
    }

    public T setChildren(@NotNull List<T> children) {
        this.children = children;
        return getThis();
    }

    public T addChild(@NotNull T child) {
        children.add(child);
        return getThis();
    }

    public T addChildren(@NotNull List<T> children) {
        for (T child : children) {
            addChild(child);
        }
        return getThis();
    }

    public T removeChild(T child) {
        children.remove(child);
        return getThis();
    }

    public D getData() {
        return data;
    }

    public T setData(D data) {
        this.data = data;
        return getThis();
    }

}
