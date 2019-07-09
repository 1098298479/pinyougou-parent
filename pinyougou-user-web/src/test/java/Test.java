

public class Test{

    public static void main(String[] args) {
        System.out.print(new BX().c);
    }
}

class AX {

    static  {
        System.out.print("A");
    }

    {
        System.out.print("--A--");
    }

    AX(){
        System.out.print("---AA---");
    }
}

class BX extends AX {

    static {
        System.out.print("B");
    }

    BX() {
        System.out.print("---BB---");
    }

    {
        System.out.print("--B--");
    }

    public    static String c="C";
}