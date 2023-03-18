<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" style="width: 100%;" alt="">
                    </div>
                </div>
            </div>

            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                       <span style="font-size: 20px;">我的Bot</span>
                       <button type="button" class="btn btn-primary btn-sm float-end" data-bs-toggle="modal" data-bs-target="#bot_add_btn">添加Bot</button>
                        <div class="modal fade modal-xl" id="bot_add_btn" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5">创建Bot</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                    <div class="modal-body">
                                        <form>
                                            <div class="mb-3">
                                                <label for="add-bot-title" class="form-label">名称</label>
                                                <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-discription" class="form-label">Bot简介</label>
                                                <textarea v-model="botadd.description" type="text" class="form-control" id="add-bot-discription" placeholder="请输入Bot简介"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-code" class="form-label">Bot代码</label>
                                                <VAceEditor
                                                    v-model:value="botadd.content"
                                                    @init="editorInit"
                                                    lang="c_cpp"
                                                    theme="textmate"
                                                    style="height: 300px" 
                                                    :options="{
                                                        enableBasicAutocompletion: true, //启用基本自动完成
                                                        enableSnippets: true, // 启用代码段
                                                        enableLiveAutocompletion: true, // 启用实时自动完成
                                                        fontSize: 16, //设置字号
                                                        tabSize: 2, // 标签大小
                                                        showPrintMargin: false, //去除编辑器里的竖线
                                                        highlightActiveLine: false,
                                                        
                                                    }"
                                                />
                                            </div>
                                            <div class="modal-footer">
                                                <div class="error_message">{{ botadd.error_message }}</div>
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                <button  type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Bot名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update_bot'+bot.id">修改</button>
                                        <button type="button" class="btn btn-danger" @click="delete_bot(bot)">删除</button>

                                        <div class="modal fade modal-xl" :id="'update_bot'+bot.id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                            <div class="modal-dialog">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h1 class="modal-title fs-5" >创建Bot</h1>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <form>
                                                            <div class="mb-3">
                                                                <label for="add-bot-title" class="form-label">名称</label>
                                                                <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-discription" class="form-label">Bot简介</label>
                                                                <textarea v-model="bot.description" type="text" class="form-control" id="add-bot-discription" placeholder="请输入Bot简介"></textarea>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-code" class="form-label">Bot代码</label>
                                                                <VAceEditor
                                                                    v-model:value="bot.content"
                                                                    @init="editorInit"
                                                                    lang="c_cpp"
                                                                    theme="textmate"
                                                                    style="height: 300px"
                                                                    :options="{
                                                                        enableBasicAutocompletion: true, //启用基本自动完成
                                                                        enableSnippets: true, // 启用代码段
                                                                        enableLiveAutocompletion: true, // 启用实时自动完成
                                                                        fontSize: 14, //设置字号
                                                                        tabSize: 2, // 标签大小
                                                                        showPrintMargin: false, //去除编辑器里的竖线
                                                                        highlightActiveLine: true,
                                                                    }"
                                                                />
                                                            </div>
                                                            <div class="modal-footer">
                                                                <div class="error_message">{{ bot.error_message }}</div>
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                                <button  type="button" class="btn btn-primary" @click="update_bot(bot)">保存</button>
                                                            </div>
                                                        </form>
                                                        
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive} from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';
import { Modal } from 'bootstrap/dist/js/bootstrap';
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';
export default {
    components: {
        VAceEditor,
    },
    setup(){
           ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" +
            require("ace-builds").version +
            "/src-noconflict/");
        const store = useStore();
        let bots=ref([]);
        const refresh_bots=()=>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/getlist/",
                type:"GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    bots.value = resp;
                }
            });
        };
        const botadd=reactive({
            title:"",
            description:"",
            content:"",
            error_message:"",
        });

        refresh_bots();
        
        const add_bot=()=>{
            botadd.error_message="";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/add/",
                type:"POST",
                data:{
                    title:botadd.title,
                    description:botadd.description,
                    content:botadd.content,
                },
                headers:{
                    Authorization:"Bearer "+store.state.user.token,
                },
                success(resp){
                    if (resp.error_message === "success"){
                        botadd.title="";
                        botadd.description="";
                        botadd.content="";
                        refresh_bots();
                        Modal.getInstance("#bot_add_btn").hide();
                    }else{
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }
        
        const delete_bot=(bot)=>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/remove/",
                type:"POST",
                data:{
                    bot_id:bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if (resp.error_message === "success"){
                        refresh_bots();
                    }
                }
            });
        }

        const update_bot=(bot)=>{
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/update/",
                type: "POST",
                data: {
                    bot_id:bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                        Modal.getInstance("#update_bot"+bot.id).hide();
                    } else {
                        bot.error_message=resp.error_message;
                    }
                }
            })
        }

        return {
            bots,
            botadd,
            add_bot,
            delete_bot,
            update_bot,
        }
    }
}
</script>

<style scoped>
.error_message{
    color: red;
}
</style>