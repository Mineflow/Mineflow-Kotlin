package tokyo.aieuo.mineflow.economy

interface EconomyLoader {

    fun getMoney(name: String): Int

    fun addMoney(name: String, money: Int)

    fun takeMoney(name: String, money: Int)

    fun setMoney(name: String, money: Int)
}