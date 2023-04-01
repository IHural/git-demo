import java.util.List;


public class CarouselRun {
    private final List<Integer> list;
    private int current;

    public CarouselRun(List<Integer> list) {
        this.list = list;
        this.current = 0;
    }

    public int next() {
        while (this.current < this.list.size()) {

            int next = this.list.get(this.current);
            if (next > 0) {
                current = next;
                current--;
            this.list.set(this.current, current);
            this.current++;
            return current;
        }
            this.current++;

        }
        return -1;
    }


    public boolean isFinished() {
        for (int i : this.list) {
            if (i > 0) {
                return false;
            }
        }
        return true;
    }
}







//    public int next() {
//       throw new UnsupportedOperationException();
//    }
//
//    public boolean isFinished() {
//        throw new UnsupportedOperationException();
//    }


