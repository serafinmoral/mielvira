interface ConditionalIndependence {

    boolean independents(Node x, Node y, NodeList z);
    boolean independents(Node x, Node y, NodeList z, int degree);

}
