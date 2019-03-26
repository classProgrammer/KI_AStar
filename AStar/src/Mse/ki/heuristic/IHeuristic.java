package Mse.ki.heuristic;

public interface IHeuristic<ValueType, ArgumentType> {
    ValueType estimate(ArgumentType current, ArgumentType goal);
}
