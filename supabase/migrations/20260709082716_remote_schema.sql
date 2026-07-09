drop extension if exists "pg_net";

revoke delete on table "public"."authors" from "anon";

revoke insert on table "public"."authors" from "anon";

revoke references on table "public"."authors" from "anon";

revoke select on table "public"."authors" from "anon";

revoke trigger on table "public"."authors" from "anon";

revoke truncate on table "public"."authors" from "anon";

revoke update on table "public"."authors" from "anon";

revoke delete on table "public"."authors" from "authenticated";

revoke insert on table "public"."authors" from "authenticated";

revoke references on table "public"."authors" from "authenticated";

revoke select on table "public"."authors" from "authenticated";

revoke trigger on table "public"."authors" from "authenticated";

revoke truncate on table "public"."authors" from "authenticated";

revoke update on table "public"."authors" from "authenticated";

revoke delete on table "public"."authors" from "service_role";

revoke insert on table "public"."authors" from "service_role";

revoke references on table "public"."authors" from "service_role";

revoke select on table "public"."authors" from "service_role";

revoke trigger on table "public"."authors" from "service_role";

revoke truncate on table "public"."authors" from "service_role";

revoke update on table "public"."authors" from "service_role";

revoke delete on table "public"."clients" from "anon";

revoke insert on table "public"."clients" from "anon";

revoke references on table "public"."clients" from "anon";

revoke select on table "public"."clients" from "anon";

revoke trigger on table "public"."clients" from "anon";

revoke truncate on table "public"."clients" from "anon";

revoke update on table "public"."clients" from "anon";

revoke delete on table "public"."clients" from "authenticated";

revoke insert on table "public"."clients" from "authenticated";

revoke references on table "public"."clients" from "authenticated";

revoke select on table "public"."clients" from "authenticated";

revoke trigger on table "public"."clients" from "authenticated";

revoke truncate on table "public"."clients" from "authenticated";

revoke update on table "public"."clients" from "authenticated";

revoke delete on table "public"."clients" from "service_role";

revoke insert on table "public"."clients" from "service_role";

revoke references on table "public"."clients" from "service_role";

revoke select on table "public"."clients" from "service_role";

revoke trigger on table "public"."clients" from "service_role";

revoke truncate on table "public"."clients" from "service_role";

revoke update on table "public"."clients" from "service_role";

revoke delete on table "public"."collection_images" from "anon";

revoke insert on table "public"."collection_images" from "anon";

revoke references on table "public"."collection_images" from "anon";

revoke select on table "public"."collection_images" from "anon";

revoke trigger on table "public"."collection_images" from "anon";

revoke truncate on table "public"."collection_images" from "anon";

revoke update on table "public"."collection_images" from "anon";

revoke delete on table "public"."collection_images" from "authenticated";

revoke insert on table "public"."collection_images" from "authenticated";

revoke references on table "public"."collection_images" from "authenticated";

revoke select on table "public"."collection_images" from "authenticated";

revoke trigger on table "public"."collection_images" from "authenticated";

revoke truncate on table "public"."collection_images" from "authenticated";

revoke update on table "public"."collection_images" from "authenticated";

revoke delete on table "public"."collection_images" from "service_role";

revoke insert on table "public"."collection_images" from "service_role";

revoke references on table "public"."collection_images" from "service_role";

revoke select on table "public"."collection_images" from "service_role";

revoke trigger on table "public"."collection_images" from "service_role";

revoke truncate on table "public"."collection_images" from "service_role";

revoke update on table "public"."collection_images" from "service_role";

revoke delete on table "public"."collection_likes" from "anon";

revoke insert on table "public"."collection_likes" from "anon";

revoke references on table "public"."collection_likes" from "anon";

revoke select on table "public"."collection_likes" from "anon";

revoke trigger on table "public"."collection_likes" from "anon";

revoke truncate on table "public"."collection_likes" from "anon";

revoke update on table "public"."collection_likes" from "anon";

revoke delete on table "public"."collection_likes" from "authenticated";

revoke insert on table "public"."collection_likes" from "authenticated";

revoke references on table "public"."collection_likes" from "authenticated";

revoke select on table "public"."collection_likes" from "authenticated";

revoke trigger on table "public"."collection_likes" from "authenticated";

revoke truncate on table "public"."collection_likes" from "authenticated";

revoke update on table "public"."collection_likes" from "authenticated";

revoke delete on table "public"."collection_likes" from "service_role";

revoke insert on table "public"."collection_likes" from "service_role";

revoke references on table "public"."collection_likes" from "service_role";

revoke select on table "public"."collection_likes" from "service_role";

revoke trigger on table "public"."collection_likes" from "service_role";

revoke truncate on table "public"."collection_likes" from "service_role";

revoke update on table "public"."collection_likes" from "service_role";

revoke delete on table "public"."collection_tags" from "anon";

revoke insert on table "public"."collection_tags" from "anon";

revoke references on table "public"."collection_tags" from "anon";

revoke select on table "public"."collection_tags" from "anon";

revoke trigger on table "public"."collection_tags" from "anon";

revoke truncate on table "public"."collection_tags" from "anon";

revoke update on table "public"."collection_tags" from "anon";

revoke delete on table "public"."collection_tags" from "authenticated";

revoke insert on table "public"."collection_tags" from "authenticated";

revoke references on table "public"."collection_tags" from "authenticated";

revoke select on table "public"."collection_tags" from "authenticated";

revoke trigger on table "public"."collection_tags" from "authenticated";

revoke truncate on table "public"."collection_tags" from "authenticated";

revoke update on table "public"."collection_tags" from "authenticated";

revoke delete on table "public"."collection_tags" from "service_role";

revoke insert on table "public"."collection_tags" from "service_role";

revoke references on table "public"."collection_tags" from "service_role";

revoke select on table "public"."collection_tags" from "service_role";

revoke trigger on table "public"."collection_tags" from "service_role";

revoke truncate on table "public"."collection_tags" from "service_role";

revoke update on table "public"."collection_tags" from "service_role";

revoke delete on table "public"."collections" from "anon";

revoke insert on table "public"."collections" from "anon";

revoke references on table "public"."collections" from "anon";

revoke select on table "public"."collections" from "anon";

revoke trigger on table "public"."collections" from "anon";

revoke truncate on table "public"."collections" from "anon";

revoke update on table "public"."collections" from "anon";

revoke delete on table "public"."collections" from "authenticated";

revoke insert on table "public"."collections" from "authenticated";

revoke references on table "public"."collections" from "authenticated";

revoke select on table "public"."collections" from "authenticated";

revoke trigger on table "public"."collections" from "authenticated";

revoke truncate on table "public"."collections" from "authenticated";

revoke update on table "public"."collections" from "authenticated";

revoke delete on table "public"."collections" from "service_role";

revoke insert on table "public"."collections" from "service_role";

revoke references on table "public"."collections" from "service_role";

revoke select on table "public"."collections" from "service_role";

revoke trigger on table "public"."collections" from "service_role";

revoke truncate on table "public"."collections" from "service_role";

revoke update on table "public"."collections" from "service_role";

revoke delete on table "public"."comments" from "anon";

revoke insert on table "public"."comments" from "anon";

revoke references on table "public"."comments" from "anon";

revoke select on table "public"."comments" from "anon";

revoke trigger on table "public"."comments" from "anon";

revoke truncate on table "public"."comments" from "anon";

revoke update on table "public"."comments" from "anon";

revoke delete on table "public"."comments" from "authenticated";

revoke insert on table "public"."comments" from "authenticated";

revoke references on table "public"."comments" from "authenticated";

revoke select on table "public"."comments" from "authenticated";

revoke trigger on table "public"."comments" from "authenticated";

revoke truncate on table "public"."comments" from "authenticated";

revoke update on table "public"."comments" from "authenticated";

revoke delete on table "public"."comments" from "service_role";

revoke insert on table "public"."comments" from "service_role";

revoke references on table "public"."comments" from "service_role";

revoke select on table "public"."comments" from "service_role";

revoke trigger on table "public"."comments" from "service_role";

revoke truncate on table "public"."comments" from "service_role";

revoke update on table "public"."comments" from "service_role";

revoke delete on table "public"."email_verification_token" from "anon";

revoke insert on table "public"."email_verification_token" from "anon";

revoke references on table "public"."email_verification_token" from "anon";

revoke select on table "public"."email_verification_token" from "anon";

revoke trigger on table "public"."email_verification_token" from "anon";

revoke truncate on table "public"."email_verification_token" from "anon";

revoke update on table "public"."email_verification_token" from "anon";

revoke delete on table "public"."email_verification_token" from "authenticated";

revoke insert on table "public"."email_verification_token" from "authenticated";

revoke references on table "public"."email_verification_token" from "authenticated";

revoke select on table "public"."email_verification_token" from "authenticated";

revoke trigger on table "public"."email_verification_token" from "authenticated";

revoke truncate on table "public"."email_verification_token" from "authenticated";

revoke update on table "public"."email_verification_token" from "authenticated";

revoke delete on table "public"."email_verification_token" from "service_role";

revoke insert on table "public"."email_verification_token" from "service_role";

revoke references on table "public"."email_verification_token" from "service_role";

revoke select on table "public"."email_verification_token" from "service_role";

revoke trigger on table "public"."email_verification_token" from "service_role";

revoke truncate on table "public"."email_verification_token" from "service_role";

revoke update on table "public"."email_verification_token" from "service_role";

revoke delete on table "public"."images" from "anon";

revoke insert on table "public"."images" from "anon";

revoke references on table "public"."images" from "anon";

revoke select on table "public"."images" from "anon";

revoke trigger on table "public"."images" from "anon";

revoke truncate on table "public"."images" from "anon";

revoke update on table "public"."images" from "anon";

revoke delete on table "public"."images" from "authenticated";

revoke insert on table "public"."images" from "authenticated";

revoke references on table "public"."images" from "authenticated";

revoke select on table "public"."images" from "authenticated";

revoke trigger on table "public"."images" from "authenticated";

revoke truncate on table "public"."images" from "authenticated";

revoke update on table "public"."images" from "authenticated";

revoke delete on table "public"."images" from "service_role";

revoke insert on table "public"."images" from "service_role";

revoke references on table "public"."images" from "service_role";

revoke select on table "public"."images" from "service_role";

revoke trigger on table "public"."images" from "service_role";

revoke truncate on table "public"."images" from "service_role";

revoke update on table "public"."images" from "service_role";

revoke delete on table "public"."media_resources" from "anon";

revoke insert on table "public"."media_resources" from "anon";

revoke references on table "public"."media_resources" from "anon";

revoke select on table "public"."media_resources" from "anon";

revoke trigger on table "public"."media_resources" from "anon";

revoke truncate on table "public"."media_resources" from "anon";

revoke update on table "public"."media_resources" from "anon";

revoke delete on table "public"."media_resources" from "authenticated";

revoke insert on table "public"."media_resources" from "authenticated";

revoke references on table "public"."media_resources" from "authenticated";

revoke select on table "public"."media_resources" from "authenticated";

revoke trigger on table "public"."media_resources" from "authenticated";

revoke truncate on table "public"."media_resources" from "authenticated";

revoke update on table "public"."media_resources" from "authenticated";

revoke delete on table "public"."media_resources" from "service_role";

revoke insert on table "public"."media_resources" from "service_role";

revoke references on table "public"."media_resources" from "service_role";

revoke select on table "public"."media_resources" from "service_role";

revoke trigger on table "public"."media_resources" from "service_role";

revoke truncate on table "public"."media_resources" from "service_role";

revoke update on table "public"."media_resources" from "service_role";

revoke delete on table "public"."password_reset_tokens" from "anon";

revoke insert on table "public"."password_reset_tokens" from "anon";

revoke references on table "public"."password_reset_tokens" from "anon";

revoke select on table "public"."password_reset_tokens" from "anon";

revoke trigger on table "public"."password_reset_tokens" from "anon";

revoke truncate on table "public"."password_reset_tokens" from "anon";

revoke update on table "public"."password_reset_tokens" from "anon";

revoke delete on table "public"."password_reset_tokens" from "authenticated";

revoke insert on table "public"."password_reset_tokens" from "authenticated";

revoke references on table "public"."password_reset_tokens" from "authenticated";

revoke select on table "public"."password_reset_tokens" from "authenticated";

revoke trigger on table "public"."password_reset_tokens" from "authenticated";

revoke truncate on table "public"."password_reset_tokens" from "authenticated";

revoke update on table "public"."password_reset_tokens" from "authenticated";

revoke delete on table "public"."password_reset_tokens" from "service_role";

revoke insert on table "public"."password_reset_tokens" from "service_role";

revoke references on table "public"."password_reset_tokens" from "service_role";

revoke select on table "public"."password_reset_tokens" from "service_role";

revoke trigger on table "public"."password_reset_tokens" from "service_role";

revoke truncate on table "public"."password_reset_tokens" from "service_role";

revoke update on table "public"."password_reset_tokens" from "service_role";

revoke delete on table "public"."purchase_items" from "anon";

revoke insert on table "public"."purchase_items" from "anon";

revoke references on table "public"."purchase_items" from "anon";

revoke select on table "public"."purchase_items" from "anon";

revoke trigger on table "public"."purchase_items" from "anon";

revoke truncate on table "public"."purchase_items" from "anon";

revoke update on table "public"."purchase_items" from "anon";

revoke delete on table "public"."purchase_items" from "authenticated";

revoke insert on table "public"."purchase_items" from "authenticated";

revoke references on table "public"."purchase_items" from "authenticated";

revoke select on table "public"."purchase_items" from "authenticated";

revoke trigger on table "public"."purchase_items" from "authenticated";

revoke truncate on table "public"."purchase_items" from "authenticated";

revoke update on table "public"."purchase_items" from "authenticated";

revoke delete on table "public"."purchase_items" from "service_role";

revoke insert on table "public"."purchase_items" from "service_role";

revoke references on table "public"."purchase_items" from "service_role";

revoke select on table "public"."purchase_items" from "service_role";

revoke trigger on table "public"."purchase_items" from "service_role";

revoke truncate on table "public"."purchase_items" from "service_role";

revoke update on table "public"."purchase_items" from "service_role";

revoke delete on table "public"."purchases" from "anon";

revoke insert on table "public"."purchases" from "anon";

revoke references on table "public"."purchases" from "anon";

revoke select on table "public"."purchases" from "anon";

revoke trigger on table "public"."purchases" from "anon";

revoke truncate on table "public"."purchases" from "anon";

revoke update on table "public"."purchases" from "anon";

revoke delete on table "public"."purchases" from "authenticated";

revoke insert on table "public"."purchases" from "authenticated";

revoke references on table "public"."purchases" from "authenticated";

revoke select on table "public"."purchases" from "authenticated";

revoke trigger on table "public"."purchases" from "authenticated";

revoke truncate on table "public"."purchases" from "authenticated";

revoke update on table "public"."purchases" from "authenticated";

revoke delete on table "public"."purchases" from "service_role";

revoke insert on table "public"."purchases" from "service_role";

revoke references on table "public"."purchases" from "service_role";

revoke select on table "public"."purchases" from "service_role";

revoke trigger on table "public"."purchases" from "service_role";

revoke truncate on table "public"."purchases" from "service_role";

revoke update on table "public"."purchases" from "service_role";

revoke delete on table "public"."refresh_token" from "anon";

revoke insert on table "public"."refresh_token" from "anon";

revoke references on table "public"."refresh_token" from "anon";

revoke select on table "public"."refresh_token" from "anon";

revoke trigger on table "public"."refresh_token" from "anon";

revoke truncate on table "public"."refresh_token" from "anon";

revoke update on table "public"."refresh_token" from "anon";

revoke delete on table "public"."refresh_token" from "authenticated";

revoke insert on table "public"."refresh_token" from "authenticated";

revoke references on table "public"."refresh_token" from "authenticated";

revoke select on table "public"."refresh_token" from "authenticated";

revoke trigger on table "public"."refresh_token" from "authenticated";

revoke truncate on table "public"."refresh_token" from "authenticated";

revoke update on table "public"."refresh_token" from "authenticated";

revoke delete on table "public"."refresh_token" from "service_role";

revoke insert on table "public"."refresh_token" from "service_role";

revoke references on table "public"."refresh_token" from "service_role";

revoke select on table "public"."refresh_token" from "service_role";

revoke trigger on table "public"."refresh_token" from "service_role";

revoke truncate on table "public"."refresh_token" from "service_role";

revoke update on table "public"."refresh_token" from "service_role";

revoke delete on table "public"."tags" from "anon";

revoke insert on table "public"."tags" from "anon";

revoke references on table "public"."tags" from "anon";

revoke select on table "public"."tags" from "anon";

revoke trigger on table "public"."tags" from "anon";

revoke truncate on table "public"."tags" from "anon";

revoke update on table "public"."tags" from "anon";

revoke delete on table "public"."tags" from "authenticated";

revoke insert on table "public"."tags" from "authenticated";

revoke references on table "public"."tags" from "authenticated";

revoke select on table "public"."tags" from "authenticated";

revoke trigger on table "public"."tags" from "authenticated";

revoke truncate on table "public"."tags" from "authenticated";

revoke update on table "public"."tags" from "authenticated";

revoke delete on table "public"."tags" from "service_role";

revoke insert on table "public"."tags" from "service_role";

revoke references on table "public"."tags" from "service_role";

revoke select on table "public"."tags" from "service_role";

revoke trigger on table "public"."tags" from "service_role";

revoke truncate on table "public"."tags" from "service_role";

revoke update on table "public"."tags" from "service_role";

revoke delete on table "public"."users" from "anon";

revoke insert on table "public"."users" from "anon";

revoke references on table "public"."users" from "anon";

revoke select on table "public"."users" from "anon";

revoke trigger on table "public"."users" from "anon";

revoke truncate on table "public"."users" from "anon";

revoke update on table "public"."users" from "anon";

revoke delete on table "public"."users" from "authenticated";

revoke insert on table "public"."users" from "authenticated";

revoke references on table "public"."users" from "authenticated";

revoke select on table "public"."users" from "authenticated";

revoke trigger on table "public"."users" from "authenticated";

revoke truncate on table "public"."users" from "authenticated";

revoke update on table "public"."users" from "authenticated";

revoke delete on table "public"."users" from "service_role";

revoke insert on table "public"."users" from "service_role";

revoke references on table "public"."users" from "service_role";

revoke select on table "public"."users" from "service_role";

revoke trigger on table "public"."users" from "service_role";

revoke truncate on table "public"."users" from "service_role";

revoke update on table "public"."users" from "service_role";


