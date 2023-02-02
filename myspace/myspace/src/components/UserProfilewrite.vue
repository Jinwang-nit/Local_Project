<template>
    <div class="card write-card">
        <div class="card-body">
            <label for="write-post" class="form-label">发帖</label>
            <textarea v-model="content" class="form-control" id="write-post" rows="3"></textarea> <!--v-model将文本内容和cotent绑定起来-->
            <button @click="post_a_post" type="button" class="btn btn-primary btn-sm">发送</button>
        </div>
    </div>
</template>

<script>
import {ref} from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';
export default{
    name:"UserProfilewrite",
    setup(props,context){
        let content=ref('');
        const store = useStore();
        const post_a_post=()=>{
            $.ajax({
                url:"https://app165.acapp.acwing.com.cn/myspace/post/",
                type:"POST",
                data:{
                    content:content.value,
                },
                headers:{
                    'Authorization': "Bearer " + store.state.user.access
                },
                success(resp){  
                    if (resp.result === "success"){
                        context.emit('post_a_post', content.value);
                        content.value = "";
                    }
                }
            });
        }
        return{
            content,
            post_a_post,
        }
    },
}
</script>

<style>
.write-card{
    margin-top: 20px;
}
button{
    margin-top: 10px;
}
</style>