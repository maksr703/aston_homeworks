public class Main {
    public static void main(String[] args) {
        MapCustom<String, Integer> map = new HashMapCustom<>();
        Object view;

        map.put("Apple", 5);
        map.put("Banana", 4);
        map.put("Cherry", 3);

        view = map.put("Eggplant", 1);
        System.out.println(view); // null

        view = map.put("Banana", 10);
        System.out.println(view); // 4
        System.out.println(map.get("Banana")); // 10

        map.remove("Cherry");
        System.out.println(map.get("Cherry")); // null

        System.out.println(map.get("Apple")); // 5

        System.out.println(map.get("Blackberry")); // null
    }
}
