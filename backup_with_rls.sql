


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;




ALTER SCHEMA "public" OWNER TO "postgres";


CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";






CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";






CREATE TYPE "public"."media_file_type" AS ENUM (
    'video',
    'image',
    'archive',
    'document'
);


ALTER TYPE "public"."media_file_type" OWNER TO "postgres";


CREATE TYPE "public"."media_mime_type" AS ENUM (
    'jpeg',
    'png',
    'mp4',
    'zip',
    'rar',
    'pdf',
    'webp'
);


ALTER TYPE "public"."media_mime_type" OWNER TO "postgres";


CREATE TYPE "public"."media_provider" AS ENUM (
    'local',
    'google_drive',
    'supabase'
);


ALTER TYPE "public"."media_provider" OWNER TO "postgres";


CREATE TYPE "public"."purchase_status" AS ENUM (
    'pending',
    'succeeded',
    'failed',
    'refunded'
);


ALTER TYPE "public"."purchase_status" OWNER TO "postgres";


CREATE TYPE "public"."token_status" AS ENUM (
    'PENDING',
    'VERIFIED',
    'EXPIRED',
    'pending',
    'verified',
    'expired'
);


ALTER TYPE "public"."token_status" OWNER TO "postgres";


CREATE TYPE "public"."token_type" AS ENUM (
    'verify_email',
    'reset_password',
    'change_email',
    'change_password'
);


ALTER TYPE "public"."token_type" OWNER TO "postgres";


CREATE CAST (character varying AS "public"."media_file_type") WITH INOUT AS IMPLICIT;



CREATE CAST (character varying AS "public"."media_mime_type") WITH INOUT AS IMPLICIT;



CREATE CAST (character varying AS "public"."media_provider") WITH INOUT AS IMPLICIT;


SET default_tablespace = '';

SET default_table_access_method = "heap";


CREATE TABLE IF NOT EXISTS "public"."authors" (
    "users_id" bigint NOT NULL,
    "bio" "text" NOT NULL,
    "social_links" "jsonb" NOT NULL,
    "rating" numeric(10,2) NOT NULL,
    "total_sales" integer NOT NULL,
    "stripe_account_id" "text",
    "id" bigint NOT NULL
);


ALTER TABLE "public"."authors" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."authors_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."authors_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."authors_id_seq" OWNED BY "public"."authors"."id";



CREATE TABLE IF NOT EXISTS "public"."clients" (
    "users_id" bigint NOT NULL,
    "newsletter_subscribed" boolean NOT NULL,
    "preferred_language" "text" NOT NULL,
    "id" bigint NOT NULL
);


ALTER TABLE "public"."clients" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."clients_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."clients_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."clients_id_seq" OWNED BY "public"."clients"."id";



CREATE TABLE IF NOT EXISTS "public"."collection_images" (
    "id" bigint NOT NULL,
    "collection_id" bigint NOT NULL,
    "image_id" bigint NOT NULL
);


ALTER TABLE "public"."collection_images" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."collection_images_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."collection_images_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."collection_images_id_seq" OWNED BY "public"."collection_images"."id";



CREATE TABLE IF NOT EXISTS "public"."collection_likes" (
    "id" bigint NOT NULL,
    "collection_id" bigint NOT NULL,
    "user_id" bigint NOT NULL,
    "created_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "public"."collection_likes" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."collection_likes_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."collection_likes_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."collection_likes_id_seq" OWNED BY "public"."collection_likes"."id";



CREATE TABLE IF NOT EXISTS "public"."collection_tags" (
    "tags_id" bigint NOT NULL,
    "collections_id" bigint NOT NULL,
    "id" bigint NOT NULL
);


ALTER TABLE "public"."collection_tags" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."collection_tags_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."collection_tags_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."collection_tags_id_seq" OWNED BY "public"."collection_tags"."id";



CREATE TABLE IF NOT EXISTS "public"."collections" (
    "id" bigint NOT NULL,
    "name" character varying(50) NOT NULL,
    "description" "text" NOT NULL,
    "authors_id" bigint NOT NULL,
    "price" numeric(10,2) NOT NULL,
    "video_tutorial_url" "text" NOT NULL,
    "project_file_id" bigint,
    "external_link" character varying(512) DEFAULT NULL::character varying
);


ALTER TABLE "public"."collections" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."collections_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."collections_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."collections_id_seq" OWNED BY "public"."collections"."id";



CREATE TABLE IF NOT EXISTS "public"."comments" (
    "id" bigint NOT NULL,
    "collection_id" bigint NOT NULL,
    "user_id" bigint NOT NULL,
    "content" "text" NOT NULL,
    "created_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE "public"."comments" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."comments_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."comments_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."comments_id_seq" OWNED BY "public"."comments"."id";



CREATE TABLE IF NOT EXISTS "public"."email_verification_token" (
    "id" bigint NOT NULL,
    "user_id" bigint NOT NULL,
    "token" "text" NOT NULL,
    "type" "text" NOT NULL,
    "expires_at" timestamp with time zone NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "status" "text" DEFAULT 'pending'::"public"."token_status" NOT NULL,
    "new_email" character varying(255),
    "attempt_count" integer DEFAULT 0 NOT NULL,
    "blocked_until" timestamp with time zone,
    "resend_count" integer DEFAULT 0 NOT NULL
);


ALTER TABLE "public"."email_verification_token" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."email_verification_token_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."email_verification_token_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."email_verification_token_id_seq" OWNED BY "public"."email_verification_token"."id";



CREATE TABLE IF NOT EXISTS "public"."images" (
    "id" bigint NOT NULL,
    "file_name" "text" NOT NULL,
    "file_path" "text" NOT NULL,
    "mime_type" "public"."media_mime_type" NOT NULL,
    "width" integer NOT NULL,
    "height" integer NOT NULL,
    "file_size" bigint NOT NULL
);


ALTER TABLE "public"."images" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."images_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."images_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."images_id_seq" OWNED BY "public"."images"."id";



CREATE TABLE IF NOT EXISTS "public"."media_resources" (
    "id" bigint NOT NULL,
    "file_name" "text" NOT NULL,
    "file_path" "text" NOT NULL,
    "file_type" "public"."media_file_type" NOT NULL,
    "mime_type" "public"."media_mime_type" NOT NULL,
    "provider" "public"."media_provider" NOT NULL,
    "file_size" bigint NOT NULL
);


ALTER TABLE "public"."media_resources" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."media_resources_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."media_resources_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."media_resources_id_seq" OWNED BY "public"."media_resources"."id";



CREATE TABLE IF NOT EXISTS "public"."password_reset_tokens" (
    "id" bigint NOT NULL,
    "user_id" bigint NOT NULL,
    "code" "text" NOT NULL,
    "new_password_hash" "text" NOT NULL,
    "type" "text" NOT NULL,
    "resend_count" integer DEFAULT 0 NOT NULL,
    "attempt_count" integer DEFAULT 0 NOT NULL,
    "expires_at" timestamp with time zone NOT NULL,
    "created_at" timestamp with time zone DEFAULT "now"() NOT NULL,
    "blocked_until" timestamp with time zone
);


ALTER TABLE "public"."password_reset_tokens" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."password_reset_tokens_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."password_reset_tokens_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."password_reset_tokens_id_seq" OWNED BY "public"."password_reset_tokens"."id";



CREATE TABLE IF NOT EXISTS "public"."purchase_items" (
    "id" bigint NOT NULL,
    "purchase_id" bigint NOT NULL,
    "collection_id" bigint NOT NULL,
    "snapshot_price" numeric(10,2) NOT NULL,
    "created_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE "public"."purchase_items" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."purchase_items_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."purchase_items_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."purchase_items_id_seq" OWNED BY "public"."purchase_items"."id";



CREATE TABLE IF NOT EXISTS "public"."purchases" (
    "id" bigint NOT NULL,
    "user_id" bigint NOT NULL,
    "stripe_payment_intent_id" "text",
    "amount" numeric(10,2) NOT NULL,
    "currency" character varying(5) NOT NULL,
    "status" character varying(50) NOT NULL,
    "created_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updated_at" timestamp with time zone NOT NULL,
    "stripe_session_id" "text" NOT NULL
);


ALTER TABLE "public"."purchases" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."purchases_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."purchases_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."purchases_id_seq" OWNED BY "public"."purchases"."id";



CREATE TABLE IF NOT EXISTS "public"."refresh_token" (
    "id" bigint NOT NULL,
    "users_id" bigint NOT NULL,
    "token" "text" NOT NULL,
    "issued_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "expiration_time" timestamp with time zone NOT NULL
);


ALTER TABLE "public"."refresh_token" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."refresh_token_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."refresh_token_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."refresh_token_id_seq" OWNED BY "public"."refresh_token"."id";



CREATE TABLE IF NOT EXISTS "public"."tags" (
    "id" bigint NOT NULL,
    "name" "text" NOT NULL
);


ALTER TABLE "public"."tags" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."tags_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."tags_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."tags_id_seq" OWNED BY "public"."tags"."id";



CREATE TABLE IF NOT EXISTS "public"."users" (
    "id" bigint NOT NULL,
    "username" character varying(64) NOT NULL,
    "email" character varying(255) NOT NULL,
    "roles" character varying(32)[] NOT NULL,
    "password_hash" "text" NOT NULL,
    "avatar_url" "text",
    "status" character varying(32) NOT NULL,
    "last_login_at" timestamp with time zone,
    "created_at" timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "is_setup_completed" boolean DEFAULT false,
    CONSTRAINT "user_status_check" CHECK ((("status")::"text" = ANY ((ARRAY['active'::character varying, 'inactive'::character varying, 'banned'::character varying])::"text"[])))
);


ALTER TABLE "public"."users" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."users_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."users_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."users_id_seq" OWNED BY "public"."users"."id";



ALTER TABLE ONLY "public"."authors" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."authors_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."clients" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."clients_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."collection_images" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."collection_images_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."collection_likes" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."collection_likes_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."collection_tags" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."collection_tags_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."collections" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."collections_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."comments" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."comments_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."email_verification_token" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."email_verification_token_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."images" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."images_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."media_resources" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."media_resources_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."password_reset_tokens" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."password_reset_tokens_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."purchase_items" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."purchase_items_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."purchases" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."purchases_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."refresh_token" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."refresh_token_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."tags" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."tags_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."users" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."users_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."authors"
    ADD CONSTRAINT "authors_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."authors"
    ADD CONSTRAINT "authors_stripe_account_id_key" UNIQUE ("stripe_account_id");



ALTER TABLE ONLY "public"."authors"
    ADD CONSTRAINT "authors_users_id_unique" UNIQUE ("users_id");



ALTER TABLE ONLY "public"."clients"
    ADD CONSTRAINT "clients_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."clients"
    ADD CONSTRAINT "clients_users_id_unique" UNIQUE ("users_id");



ALTER TABLE ONLY "public"."collection_images"
    ADD CONSTRAINT "collection_images_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."collection_likes"
    ADD CONSTRAINT "collection_likes_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."collection_tags"
    ADD CONSTRAINT "collection_tags_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."collection_tags"
    ADD CONSTRAINT "collection_tags_tags_id_collections_id_key" UNIQUE ("tags_id", "collections_id");



ALTER TABLE ONLY "public"."collections"
    ADD CONSTRAINT "collections_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."collections"
    ADD CONSTRAINT "collections_video_tutorial_url_key" UNIQUE ("video_tutorial_url");



ALTER TABLE ONLY "public"."comments"
    ADD CONSTRAINT "comments_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."email_verification_token"
    ADD CONSTRAINT "email_verification_token_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."images"
    ADD CONSTRAINT "images_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."media_resources"
    ADD CONSTRAINT "media_resources_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."password_reset_tokens"
    ADD CONSTRAINT "password_reset_tokens_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."purchase_items"
    ADD CONSTRAINT "purchase_items_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."purchases"
    ADD CONSTRAINT "purchases_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."purchases"
    ADD CONSTRAINT "purchases_stripe_payment_intent_id_key" UNIQUE ("stripe_payment_intent_id");



ALTER TABLE ONLY "public"."purchases"
    ADD CONSTRAINT "purchases_stripe_session_id_key" UNIQUE ("stripe_session_id");



ALTER TABLE ONLY "public"."refresh_token"
    ADD CONSTRAINT "refresh_token_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."tags"
    ADD CONSTRAINT "tags_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."collection_likes"
    ADD CONSTRAINT "uq_collection_author_like" UNIQUE ("collection_id", "user_id");



ALTER TABLE ONLY "public"."collection_images"
    ADD CONSTRAINT "uq_collection_image" UNIQUE ("collection_id", "image_id");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_email_key" UNIQUE ("email");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_pk" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."users"
    ADD CONSTRAINT "users_username_key" UNIQUE ("username");



CREATE INDEX "idx_collection_tags_collections_id" ON "public"."collection_tags" USING "btree" ("collections_id");



CREATE INDEX "idx_collections_authors_id" ON "public"."collections" USING "btree" ("authors_id");



CREATE INDEX "idx_collections_name" ON "public"."collections" USING "btree" ("name");



CREATE INDEX "idx_collections_price" ON "public"."collections" USING "btree" ("price");



CREATE INDEX "idx_collections_project_file" ON "public"."collections" USING "btree" ("project_file_id");



CREATE INDEX "idx_comments_collection_id" ON "public"."comments" USING "btree" ("collection_id");



CREATE INDEX "idx_email_verification_tokens_token" ON "public"."email_verification_token" USING "btree" ("token");



CREATE INDEX "idx_email_verification_tokens_user_id" ON "public"."email_verification_token" USING "btree" ("user_id");



CREATE INDEX "idx_purchase_items_purchase_id" ON "public"."purchase_items" USING "btree" ("purchase_id");



CREATE INDEX "idx_purchases_clients_id" ON "public"."purchases" USING "btree" ("user_id");



CREATE INDEX "idx_purchases_created_at" ON "public"."purchases" USING "btree" ("created_at" DESC);



CREATE INDEX "idx_purchases_session_id" ON "public"."purchases" USING "btree" ("stripe_session_id");



CREATE INDEX "idx_refresh_token_expires" ON "public"."refresh_token" USING "btree" ("expiration_time");



CREATE INDEX "idx_refresh_token_token" ON "public"."refresh_token" USING "btree" ("token");



CREATE INDEX "idx_refresh_token_users_id" ON "public"."refresh_token" USING "btree" ("users_id");



CREATE INDEX "idx_tags_name" ON "public"."tags" USING "btree" ("name");



CREATE INDEX "password_reset_tokens_code_idx" ON "public"."password_reset_tokens" USING "btree" ("code");



CREATE INDEX "password_reset_tokens_user_id_idx" ON "public"."password_reset_tokens" USING "btree" ("user_id");



ALTER TABLE ONLY "public"."authors"
    ADD CONSTRAINT "authors_users" FOREIGN KEY ("users_id") REFERENCES "public"."users"("id");



ALTER TABLE ONLY "public"."clients"
    ADD CONSTRAINT "clients_users" FOREIGN KEY ("users_id") REFERENCES "public"."users"("id");



ALTER TABLE ONLY "public"."collection_tags"
    ADD CONSTRAINT "collection_tags_collections" FOREIGN KEY ("collections_id") REFERENCES "public"."collections"("id");



ALTER TABLE ONLY "public"."collection_tags"
    ADD CONSTRAINT "collection_tags_collections_id_fkey" FOREIGN KEY ("collections_id") REFERENCES "public"."collections"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."collection_tags"
    ADD CONSTRAINT "collection_tags_tags" FOREIGN KEY ("tags_id") REFERENCES "public"."tags"("id");



ALTER TABLE ONLY "public"."collections"
    ADD CONSTRAINT "collections_authors" FOREIGN KEY ("authors_id") REFERENCES "public"."authors"("id");



ALTER TABLE ONLY "public"."collections"
    ADD CONSTRAINT "collections_media_resources" FOREIGN KEY ("project_file_id") REFERENCES "public"."media_resources"("id");



ALTER TABLE ONLY "public"."collection_images"
    ADD CONSTRAINT "fk_collection" FOREIGN KEY ("collection_id") REFERENCES "public"."collections"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."comments"
    ADD CONSTRAINT "fk_comments_collection" FOREIGN KEY ("collection_id") REFERENCES "public"."collections"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."comments"
    ADD CONSTRAINT "fk_comments_user" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."collection_images"
    ADD CONSTRAINT "fk_image" FOREIGN KEY ("image_id") REFERENCES "public"."images"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."collection_likes"
    ADD CONSTRAINT "fk_like_collection" FOREIGN KEY ("collection_id") REFERENCES "public"."collections"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."collection_likes"
    ADD CONSTRAINT "fk_like_user" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."purchase_items"
    ADD CONSTRAINT "fk_purchase_items_purchase" FOREIGN KEY ("purchase_id") REFERENCES "public"."purchases"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."collection_likes"
    ADD CONSTRAINT "fk_user" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."password_reset_tokens"
    ADD CONSTRAINT "password_reset_user_fk" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."purchases"
    ADD CONSTRAINT "purchases_users_fk" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id");



ALTER TABLE ONLY "public"."refresh_token"
    ADD CONSTRAINT "refresh_token_users" FOREIGN KEY ("users_id") REFERENCES "public"."users"("id");



ALTER TABLE ONLY "public"."email_verification_token"
    ADD CONSTRAINT "user_id_fk" FOREIGN KEY ("user_id") REFERENCES "public"."users"("id") ON DELETE CASCADE;





ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";


REVOKE USAGE ON SCHEMA "public" FROM PUBLIC;




































































































































































































