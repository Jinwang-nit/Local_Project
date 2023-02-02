<template>
    <div class="card">
        <div class="card-body">
            <div v-for="post in posts.posts" :key="post.id">
               <div class="card postcard">
                <div class="card-body">
                    {{ post.content }}
                    <button @click="delete_a_post(post.id)" v-if="is_me" type="button" class="btn btn-danger btn-sm">删除</button>
                </div>
               </div>
            </div>

        </div>
    </div>
</template>

<script>
import { computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import $ from 'jquery';
export default{
    name:"UserProfilepsost",
    props:{
        user:{
            type:Object,
            required:true,
        },
        posts:{
            type:Object,
            required:true,
        }
    },
    setup(props,context){
        const route = useRoute();
        const userId = parseInt(route.params.userId);
        const store = useStore();


        const delete_a_post=(post_id)=>{
            $.ajax({
                url:"https://app165.acapp.acwing.com.cn/myspace/post/",
                type:"DELETE",
                headers:{
                    'Authorization': "Bearer " + store.state.user.access,
                },
                data:{
                    post_id,
                },
                success(resp){
                    if (resp.result === "success"){
                        context.emit('delete_a_post', post_id);
                    }
                }
            })
        }


        const is_me = computed(() => {
            if (userId === store.state.user.id)
                return true;
            else
                return false;
        });
        return {
            is_me,
            delete_a_post,
        }
    }
}
</script>

<style>
.postcard{
    margin-bottom: 10px;
}
button{
    float: right;
}
</style>