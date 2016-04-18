package edu.udayton.coopere5.spice;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;

/**
 * Class containing many static methods required to perform nodal analysis.
 *
 * @author Evan Cooper
 * @see Jama.Matrix
 */
public class NodeAnalysis {
	// TODO: indicate somehow in the solution matrix what each value corresponds
	// to
	// could do a String[] corresponding to the system

	/**
	 * Generates the solution Matrix of a circuit defined by
	 * <code>components</code> using {@link #buildsystem(List)}. Solution Matrix
	 * is defined as <code>x=inv(A)*z</code>.
	 *
	 * @param components
	 *            - List object containing all circuit components.
	 * @return - solution matrix
	 * @see #buildsystem(List)
	 */
	public static Matrix solver(List<CircuitComponent> components) {
		Matrix[] system = buildsystem(components);
		Matrix solution = system[0].inverse().times(system[1]);
		return solution;
	}

	/**
	 * Generates two Matrix objects, A and z, in an array based on the
	 * components given. A describes the system of passive elements and voltage
	 * sources, z second is a column matrix of independent current and voltage
	 * sources.
	 *
	 * @param components
	 *            - List object containing all circuit components.
	 * @return - array containing A and z
	 */
	private static Matrix[] buildsystem(List<CircuitComponent> components) {
		int numNodes = 0;
		int numSources = 0;
		int numRows;
		Matrix[] system = new Matrix[2];
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for (CircuitComponent c : components) {
			if (c.getType() != CircuitComponent.WIRE) {
				for (int i : c.getNet()) {
					if (!nodes.contains(i) && i != 0) {
						nodes.add(i);
					}
				}
				if (c.getType() == CircuitComponent.VOLTAGE) {
					numSources++;
				}
			}
		}
		numNodes = nodes.size();
		numRows = numNodes + numSources;
		system[0] = new Matrix(numRows, numRows);
		system[1] = new Matrix(numRows, 1);

		int[] node;
		int m = numNodes;
		for (CircuitComponent c : components) {
			node = c.getNet();
			switch (c.getType()) {
			case CircuitComponent.RESISTOR:
				if (node[0] - 1 >= 0) {
					system[0].set(node[0] - 1, node[0] - 1, system[0].get(node[0] - 1, node[0] - 1) + conductance(c));
				}
				if (node[1] - 1 >= 0) {
					system[0].set(node[1] - 1, node[1] - 1, system[0].get(node[1] - 1, node[1] - 1) + conductance(c));
				}
				if (node[0] - 1 >= 0 && node[1] - 1 >= 0) {
					system[0].set(node[0] - 1, node[1] - 1, system[0].get(node[0] - 1, node[1] - 1) - conductance(c));
					system[0].set(node[1] - 1, node[0] - 1, system[0].get(node[1] - 1, node[0] - 1) - conductance(c));
				}
				break;
			case CircuitComponent.VOLTAGE:
				system[1].set(m, 0, c.getValue());
				if (node[0] - 1 >= 0) {
					system[0].set(m, node[0] - 1, 1);
					system[0].set(node[0] - 1, m, 1);
				}
				if (node[1] - 1 >= 0) {
					system[0].set(m, node[1] - 1, -1);
					system[0].set(node[1] - 1, m, -1);
				}
				m++;
				break;
			case CircuitComponent.CURRENT:
				if (node[0] - 1 >= 0) {
					system[1].set(node[0] - 1, 0, system[1].get(node[0] - 1, 0) - c.getValue());
				}
				if (node[1] - 1 >= 0) {
					system[1].set(node[1] - 1, 0, system[1].get(node[1] - 1, 0) + c.getValue());
				}
				break;
			default:
				break;
			}
		}

		return system;
	}

	/**
	 * Finds the conductance of a particular component <code>r</code>
	 *
	 * @param r
	 *            the component to find the conductance of;
	 * @return the conductance of the component, <code>1/R</code>
	 */
	private static double conductance(CircuitComponent r) {
		if (r.type == CircuitComponent.RESISTOR) {
			if (r.getValue() != 0) {
				return 1 / r.getValue();
			} else {
				return Double.POSITIVE_INFINITY;
			}
		} else if (r.type == CircuitComponent.WIRE || r.type == CircuitComponent.VOLTAGE) {
			return 0;
		} else {
			return 0;
		}
	}
}
