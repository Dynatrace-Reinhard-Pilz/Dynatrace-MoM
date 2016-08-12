import java.util.function.Function;

public class Set implements Function<Integer, Boolean> {

	@Override
	public Boolean apply(Integer t) {
		return t > 0;
	}

}
