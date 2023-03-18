<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="username">
                    {{ $store.state.user.username }}
                </div>
            </div>

            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                    <option value="-1" selected>亲自上阵</option>
                    <option v-for="bot in bots" :key="bot.id" :value="bot.id">{{bot.title}}</option>

                    </select>
                </div>
            </div>

            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center; margin-top: 70px;">
                <button type="button" class="btn btn-success btn-lg" @click="click_match">{{match_btn_info}}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';
export default {
    components: {
        
    },
    setup(){
        const store = useStore();
        let bots = ref([]);
        let select_bot = ref("-1");
        store.commit("updateOpponent",{
            username:"我的对手",
            photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
        });
        let match_btn_info = ref("开始匹配");

        const click_match=()=>{
            if (match_btn_info.value === "开始匹配"){
                match_btn_info.value="取消";
                console.log(select_bot.value);
                store.state.pk.socket.send(JSON.stringify({ // 向后端发送一个请求
                    event:"start-matching",
                    bot_id:select_bot.value,
                }));
            }else{
                console.log("点击关闭");
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
                
            }
        };

        const refresh_bots = () => {
        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                bots.value = resp;
            }
            });

        };
        refresh_bots();

        return {
            match_btn_info,
            click_match,
            bots,
            select_bot,
        }
    }
}
</script>

<style scoped>
div.matchground {
    height: 70vh;
    width: 60vw;
    margin: 40px auto;
    background-color: rgba(50, 50, 50,0.5);
}
div.user-photo{
    text-align: center;
}
div.user-photo>img{
    border-radius: 50%;
    width: 20vh;
    margin-top: 50px;
}
div.username{
    text-align: center;
    font-size: 20px;
    font-weight: 600;
    color: white;
    margin-top: 10px;
}
div.user-select-bot{
    padding-top: 20vh;
}
div.user-select-bot>select{
    width: 60%;
    margin: 0 auto;
}
</style>