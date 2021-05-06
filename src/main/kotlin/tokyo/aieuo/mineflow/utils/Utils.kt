package tokyo.aieuo.mineflow.utils

import tokyo.aieuo.mineflow.variable.DummyVariable
import tokyo.aieuo.mineflow.variable.Variable

typealias SimpleCallable = () -> Unit

typealias VariableMap = Map<String, Variable<Any>>
typealias DummyVariableMap = Map<String, DummyVariable<DummyVariable.Type>>