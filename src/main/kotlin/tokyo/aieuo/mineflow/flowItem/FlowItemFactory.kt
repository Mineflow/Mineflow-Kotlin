package tokyo.aieuo.mineflow.flowItem

import tokyo.aieuo.mineflow.economy.Economy
import tokyo.aieuo.mineflow.flowItem.action.block.CreateBlockVariable
import tokyo.aieuo.mineflow.flowItem.action.command.Command
import tokyo.aieuo.mineflow.flowItem.action.command.CommandConsole
import tokyo.aieuo.mineflow.flowItem.action.common.DoNothing
import tokyo.aieuo.mineflow.flowItem.action.common.GetDate
import tokyo.aieuo.mineflow.flowItem.action.common.SendMessageToConsole
import tokyo.aieuo.mineflow.flowItem.action.entity.*
import tokyo.aieuo.mineflow.flowItem.action.event.CallCustomTrigger
import tokyo.aieuo.mineflow.flowItem.action.event.EventCancel
import tokyo.aieuo.mineflow.flowItem.action.form.SendForm
import tokyo.aieuo.mineflow.flowItem.action.form.SendInputForm
import tokyo.aieuo.mineflow.flowItem.action.form.SendMenuForm
import tokyo.aieuo.mineflow.flowItem.action.inventory.*
import tokyo.aieuo.mineflow.flowItem.action.item.*
import tokyo.aieuo.mineflow.flowItem.action.math.*
import tokyo.aieuo.mineflow.flowItem.action.player.*
import tokyo.aieuo.mineflow.flowItem.action.plugin.AddMoney
import tokyo.aieuo.mineflow.flowItem.action.plugin.GetMoney
import tokyo.aieuo.mineflow.flowItem.action.plugin.SetMoney
import tokyo.aieuo.mineflow.flowItem.action.plugin.TakeMoney
import tokyo.aieuo.mineflow.flowItem.action.scoreboard.*
import tokyo.aieuo.mineflow.flowItem.action.script.*
import tokyo.aieuo.mineflow.flowItem.action.string.EditString
import tokyo.aieuo.mineflow.flowItem.action.string.StringLength
import tokyo.aieuo.mineflow.flowItem.action.variable.*
import tokyo.aieuo.mineflow.flowItem.action.world.*
import tokyo.aieuo.mineflow.flowItem.condition.*

object FlowItemFactory {

    private val all = mutableMapOf<String, FlowItem>()

    fun init() {
        /* actions */
        register(DoNothing())
        register(EventCancel())
        register(CallCustomTrigger())
        register(GetDate())
        register(RegisterCraftingRecipe())
        register(SendMessageToConsole())
        /* message */
        register(SendMessage())
        register(SendTip())
        register(SendPopup())
        register(BroadcastMessage())
        register(SendMessageToOp())
        register(SendTitle())
        /* entity */
        register(SetNameTag())
        register(GetEntity())
        register(Teleport())
        register(Motion())
        register(MoveTo())
        register(SetYaw())
        register(SetPitch())
        register(LookAt())
        register(AddDamage())
        register(SetImmobile())
        register(UnsetImmobile())
        register(SetHealth())
        register(SetMaxHealth())
        register(SetScale())
        register(AddEffect())
        register(CreateHumanEntity())
        /* player */
        register(GetPlayerByName())
        register(SetSleeping())
        register(SetSitting())
        register(Kick())
        register(SetFood())
        register(SetGamemode())
        register(ShowBossBar())
        register(RemoveBossBar())
        register(ShowScoreboard())
        register(HideScoreboard())
        register(PlaySound())
        register(AddPermission())
        register(RemovePermission())
        register(AddXpProgress())
        register(AddXpLevel())
        register(GetTargetBlock())
        register(AllowFlight())
        register(AllowClimbWalls())
        /* item */
        register(CreateItemVariable())
        register(AddItem())
        register(SetItemInHand())
        register(RemoveItem())
        register(RemoveItemAll())
        register(SetItemDamage())
        register(SetItemCount())
        register(SetItemName())
        register(SetItemLore())
        register(AddEnchantment())
        register(EquipArmor())
        register(SetItem())
        register(ClearInventory())
        register(GetInventoryContents())
        register(GetArmorInventoryContents())
        /* script */
        register(IFAction())
        register(ElseifAction())
        register(ElseAction())
        register(RepeatAction())
        register(ForAction())
        register(ForeachAction())
        register(ForeachPosition())
        register(WhileTaskAction())
        register(Wait())
        register(CallRecipe())
        register(ExecuteRecipe())
        register(ExecuteRecipeWithEntity())
        register(SaveData())
        register(CreateConfigVariable())
        register(SetConfigData())
        register(RemoveConfigData())
        register(SaveConfigFile())
        register(ExitRecipe())
        /* calculation */
        register(FourArithmeticOperations())
        register(Calculate())
        register(Calculate2())
        register(GetPi())
        register(GetE())
        register(GenerateRandomNumber())
        register(CalculateReversePolishNotation())
        /* String */
        register(EditString())
        register(StringLength())
        /* variable */
        register(AddVariable())
        register(DeleteVariable())
        register(CreateListVariable())
        register(AddListVariable())
        register(CreateMapVariable())
        register(AddMapVariable())
        register(CreateMapVariableFromJson())
        register(DeleteListVariableContent())
        register(CreatePositionVariable())
        register(GetVariableNested())
        register(CountListVariable())
        register(JoinListVariableToString())
        /* form */
        register(SendForm())
        register(SendInputForm())
        register(SendMenuForm())
        /* command */
        register(Command())
        register(CommandConsole())
        /* block */
        register(CreateBlockVariable())
        /* level */
        register(SetBlock())
        register(GetBlock())
        register(AddParticle())
        register(PlaySoundAt())
        register(DropItem())
        register(GetDistance())
        register(GetEntitySidePosition())
        register(GenerateRandomPosition())
        register(PositionVariableAddition())
        /* scoreboard */
        register(CreateScoreboardVariable())
        register(SetScoreboardScore())
        register(SetScoreboardScoreName())
        register(IncrementScoreboardScore())
        register(DecrementScoreboardScore())
        register(RemoveScoreboardScore())
        register(RemoveScoreboardScoreName())
        /* other plugins */
        if (Economy.isPluginLoaded()) {
            register(AddMoney())
            register(TakeMoney())
            register(SetMoney())
            register(GetMoney())
        }

        /** conditions */
        /* common */
        register(CheckNothing())
        register(IsOp())
        register(IsSneaking())
        register(IsFlying())
        register(RandomNumber())
        /* money */
        register(OverMoney())
        register(LessMoney())
        register(TakeMoneyCondition())
        /* item */
        register(InHand())
        register(ExistsItem())
        register(CanAddItem())
        register(RemoveItemCondition())
        /* script */
        register(ComparisonNumber())
        register(ComparisonString())
        register(AndScript())
        register(ORScript())
        register(NotScript())
        register(NorScript())
        register(NandScript())
        register(ExistsConfigFile())
        register(ExistsConfigData())
        /* entity */
        register(IsActiveEntity())
        register(IsPlayer())
        register(IsCreature())
        register(IsActiveEntityVariable())
        register(IsPlayerVariable())
        register(IsCreatureVariable())
        register(InArea())
        /* player */
        register(Gamemode())
        register(HasPermission())
        register(IsPlayerOnline())
        register(IsPlayerOnlineByName())
        /* variable */
        register(ExistsVariable())
        register(ExistsListVariableKey())
    }

    fun get(id: String): FlowItem? {
        return all[id]?.clone()
    }

    fun getByFilter(category: String? = null, permission: Int? = null, getAction: Boolean = true, getCondition: Boolean = true): List<FlowItem> {
        val items = mutableListOf<FlowItem>()
        for ((_, item) in all) {
            if (category !== null && item.category !== category) continue
            if (permission !== null && item.permission > permission) continue
            if (!getAction && item !is Condition) continue
            if (!getCondition && (item is Condition)) continue
            items.add(item)
        }
        return items
    }

    fun register(action: FlowItem) {
        all[action.id] = action
    }
}
