<template>
    <ContentField>
        <table class="table table-striped" style="text-align: left;">
            <thead>
                <tr>
                    <th>玩家</th>
                    <th>天梯分</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in users" :key="user.id">
                    <td>
                        <img :src="user.photo" alt="" class="record-user-photo">
                        &nbsp;
                        <span class="record-username">{{ user.username }}</span>
                    </td>
                    <td>{{ user.rating }}</td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="Page navigation example">
            <ul class="pagination" style="float:right;">
                <li class="page-item" @click="click_page(-2)">
                    <a class="page-link" href="#" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>

                </li>
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number"
                    @click="click_page(page.number)">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>

                <li class="page-item" @click="click_page(-1)">
                    <a class="page-link" href="#" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>

    </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue';
import { useStore } from 'vuex';
import $ from 'jquery';
import { ref } from 'vue';
export default {
    components: {
        ContentField,
    },
    setup() {
        const store = useStore();
        let users = ref([]);
        let curret_page = 1;
        let total_users = 0;
        let pages = ref([]);
        const click_page = (page) => {
            if (page === -2) {
                page = curret_page - 1;
            } else if (page === -1) page = curret_page + 1;
            let max_pages = parseInt(Math.ceil(total_users / 3));
            if (page >= 1 && page <= max_pages) {
                pull_page(page)
            }
        }
        const updatePages = () => {
            let max_pages = parseInt(Math.ceil(total_users / 3));
            let new_pages = [];
            for (let i = curret_page - 2; i < curret_page + 2; i++) {
                if (i >= 1 && i <= max_pages) {
                    new_pages.push({
                        number: i,
                        is_active: i === curret_page ? "active" : "",
                    });
                }
            }
            pages.value = new_pages;
        }
        const pull_page = (page) => {
            curret_page = page;
            $.ajax({
                url: "http://127.0.0.1:3000/ranklist/getlist/",
                type: "GET",
                data: {
                    page,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    users.value = resp.users;
                    total_users = resp.users_count;
                    updatePages()
                },
                error(resp) {
                    console.log(resp);
                }
            })
        };

        pull_page(curret_page);

    
        

        return {
            users,
            pages,
            click_page
        }
    }
}

</script>

<style scoped>
img.record-user-photo {
    width: 4vh;
    border-radius: 50%;
}
</style>