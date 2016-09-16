package com.harryio.orainteractive.ui.chat;

import java.util.List;

public class ChatList {
    private boolean success;
    private Pagination pagination;
    private List<Data> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Pagination {
        private int page_count;
        private int current_page;
        private boolean has_next_page;
        private boolean has_prev_page;
        private int count;
        private Object limit;

        public int getPage_count() {
            return page_count;
        }

        public void setPage_count(int page_count) {
            this.page_count = page_count;
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public boolean isHas_next_page() {
            return has_next_page;
        }

        public void setHas_next_page(boolean has_next_page) {
            this.has_next_page = has_next_page;
        }

        public boolean isHas_prev_page() {
            return has_prev_page;
        }

        public void setHas_prev_page(boolean has_prev_page) {
            this.has_prev_page = has_prev_page;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Object getLimit() {
            return limit;
        }

        public void setLimit(Object limit) {
            this.limit = limit;
        }
    }

    public static class Data {
        private int id;
        private int user_id;
        private String name;
        private String created;
        private User user;
        private LastMessage last_message;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public LastMessage getLast_message() {
            return last_message;
        }

        public void setLast_message(LastMessage last_message) {
            this.last_message = last_message;
        }

        public static class LastMessage {
            private int id;
            private int user_id;
            private int chat_id;
            private String message;
            private String created;

            private User user;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUser_id() {
                return user_id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public int getChat_id() {
                return chat_id;
            }

            public void setChat_id(int chat_id) {
                this.chat_id = chat_id;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }
        }
    }
}
