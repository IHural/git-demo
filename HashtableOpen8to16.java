package com.epam.rd.autocode.hashtableopen816;


public interface HashtableOpen8to16 {
    void insert(int key, Object value);

    Object search(int key);

    void remove(int key);

    int size();

    int[] keys();

    static HashtableOpen8to16 getInstance() {
        return new HashtableOpen8to16Impl();
    }


    class HashtableOpen8to16Impl implements HashtableOpen8to16 {

        private static final int INITIAL_CAPACITY = 8;
        private static final int MAX_CAPACITY = 16;

        private HashNode[] hashTable;
        private int size;
        private int capacity;

        public HashtableOpen8to16Impl() {
            this(INITIAL_CAPACITY);
        }

        public HashtableOpen8to16Impl(int capacity) {


            this.hashTable = new HashNode[capacity];
            this.size = 0;
            this.capacity = capacity;
        }

        private static class HashNode {
            public long deletionTimestamp;
            int key;
            Object value;

            public HashNode(int key, Object value) {
                this.key = key;
                this.value = value;
            }
        }
        @Override
        public void insert(int key, Object value) {
            if (key == 0) {
                if (hashTable[0] != null && hashTable[0].key == 0 && hashTable[0].value != null) {
                    // Якщо ключ 0 вже є в таблиці з ненульовим значенням, ми не можемо вставити новий ключ 0
                    return;
                }else{
                // Інакше вставляємо ключ 0 в позицію 0
                hashTable[0] = new HashNode(0, value);
                return;}
            }
            int hashIndex = Math.abs(key) % capacity;
            int counter = 0;
            int latestDeletedIndex = -1;
            long latestDeletionTimestamp = -1;


            while (hashTable[hashIndex] != null && counter < capacity) {
                if (hashTable[hashIndex].key == key) {
                    hashTable[hashIndex].value = value;
                    return;
                } else if (hashTable[hashIndex].key == 0 &&
                        (latestDeletedIndex == -1 || hashTable[hashIndex].deletionTimestamp > latestDeletionTimestamp)) {
                    latestDeletedIndex = hashIndex;
                    latestDeletionTimestamp = hashTable[hashIndex].deletionTimestamp;
                }
                hashIndex = (hashIndex + 1) % capacity;
                counter++;
            }

            if (latestDeletedIndex != -1) {
                hashTable[latestDeletedIndex] = new HashNode(key, value);
                size++;
            } else if (size < capacity) {
                hashTable[hashIndex] = new HashNode(key, value);
                size++;
            } else{
                    // rehash the hash table
                    HashNode[] oldHashTable = hashTable;
                    capacity *= 2;
                    hashTable = new HashNode[capacity];
                    size = 0;

                if (capacity > MAX_CAPACITY) {
                        // Hashtable is full
                        throw new IllegalStateException("Hash table is full");
                    }
                    for (HashNode node : oldHashTable) {
                            if (node != null && node.key != 0) {
                                int newIndex = Math.abs(node.key) % capacity;
                                int newCounter = 0;
                                while (hashTable[newIndex] != null && newCounter < capacity) {
                                    if (hashTable[newIndex].key == node.key) {
                                        // Значення вже існує в таблиці, не перезаписувати його
                                        break;
                                    }
                                    newIndex = (newIndex +1) % capacity;
                                    newCounter++;
                                }
                                if (newCounter < capacity) {
                                    // Вставити значення, тільки якщо воно не було вже в таблиці
                                    hashTable[newIndex] = new HashNode(node.key, node.value);
                                    size++;
                                }
                            }
                        }


                    // insert the key-value pair into the new hash table
                    insert(key, value);
                }
            }

        @Override
        public Object search(int key) {
            if (key == 0 && hashTable[0] != null) {
                return hashTable[0].value;
            }

            int hashIndex = Math.abs(key) % capacity;
            int counter = 0;

            while (hashTable[hashIndex] != null && counter < capacity) {
                if (hashTable[hashIndex].key == key ) {
                    return hashTable[hashIndex].value;
                }

                hashIndex = (hashIndex + 1) % capacity;
                counter++;
            }

            return null;
        }

        @Override
        public void remove(int key) {

            int hashIndex = Math.abs(key) % capacity;
            int counter = 0;

            while (hashTable[hashIndex] != null && counter < capacity) {
                if (hashTable[hashIndex].key == key) {
                    hashTable[hashIndex].value = null;
                    hashTable[hashIndex].key = 0;
                    hashTable[hashIndex].deletionTimestamp = 0;
                    size--;

                    if (size > 0 && size <= capacity / 4 && capacity > 2) {
                        capacity = capacity / 2;
                            rehash();

                    }
                    return;
                }

                hashIndex = (hashIndex + 1) % capacity;
                counter++;
            }
        }
        private void rehash() {
            HashNode[] oldHashTable = hashTable;
            hashTable = new HashNode[capacity];
            size = 0;

            for (HashNode node : oldHashTable) {
                if (node != null && node.key != 0) {
                    insert(node.key, node.value);
                }
            }
        }


        @Override
        public int size() {
            return size;
        }

        @Override
        public int[] keys() {
            int[] keys = new int[capacity];
            int index = 0;
            for (HashNode node : hashTable) {
                if (node != null && node.key != 0) {
                    keys[index] = node.key;
                    index++;
                }else{
                    keys[index] = 0; // додано цей рядок, щоб заповнити масив нулями, якщо вузол 'null'
                    index++;
                }
            }
            return keys;
        }

    }
}

    class Main {
        public static void main(String[] args) {
            HashtableOpen8to16 hashtable = HashtableOpen8to16.getInstance();
            for (int i = 0; i < 32; i += 2) {
                hashtable.insert(i, i);
            }
            hashtable.insert(42, 42);
            hashtable.insert(16,32);

            int[] keys = hashtable.keys();
            for (int key : keys) {
                System.out.println(key);
            }

        }
    }

